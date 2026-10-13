#!/usr/bin/env bash
set -euo pipefail

guard="$(awk '
  /^          existing_release=/ { active = 1 }
  active && /^      - name:/ { exit }
  active { sub(/^          /, ""); print }
' .github/workflows/release.yml)"
[[ -n "$guard" ]] || { echo "Release tag guard not found." >&2; exit 1; }

temporary="$(mktemp -d)"
trap 'rm -f "$temporary/output" "$temporary/log"; rmdir "$temporary"' EXIT
export GITHUB_OUTPUT="$temporary/output"
export GITHUB_REPOSITORY=example/electrostatic
export GITHUB_SHA=current-commit RELEASE_VERSION=0.0.3

gh() {
  case "$1 $2" in
    "release list")
      [[ "$MOCK_GH_FAILURE" != list ]] || return 1
      printf '%s\n' "$MOCK_RELEASE"
      ;;
    "api repos/example/electrostatic/commits/0.0.3")
      [[ "$MOCK_GH_FAILURE" != api ]] || return 1
      printf '%s\n' "$MOCK_COMMIT"
      ;;
    *) echo "Unexpected gh invocation: $*" >&2; return 99 ;;
  esac
}

jq() {
  cat >/dev/null
  printf '%s\n' "$MOCK_RELEASE"
}

git() {
  [[ "$*" == "ls-remote --exit-code --tags origin refs/tags/0.0.3" ]] ||
    { echo "Unexpected git invocation: $*" >&2; return 99; }
  return "$MOCK_GIT_STATUS"
}
export -f gh jq git

check() {
  local name="$1" expected="$5"
  export MOCK_RELEASE="$2" MOCK_COMMIT="$3" MOCK_GIT_STATUS="$4" MOCK_GH_FAILURE="${6:-none}"
  : >"$GITHUB_OUTPUT"
  if bash -e -o pipefail -c "$guard" >"$temporary/log" 2>&1; then
    if [[ "$expected" == reject ]] || ! grep -qx "exists=$expected" "$GITHUB_OUTPUT"; then
      cat "$temporary/log" >&2
      echo "FAILED: $name (unexpected acceptance/output)" >&2
      exit 1
    fi
  else
    if [[ "$expected" != reject ]] || [[ -s "$GITHUB_OUTPUT" ]]; then
      cat "$temporary/log" >&2
      echo "FAILED: $name (unexpected rejection/output)" >&2
      exit 1
    fi
  fi
  echo "Passed: $name"
}

check "unused version permits a new release" "" "" 2 false
check "same-source release permits documentation-only recovery" 0.0.3 current-commit 0 true
check "different-source historical release is rejected" 0.0.3 historical-commit 0 reject
check "tag without a GitHub Release is rejected" "" "" 0 reject
check "remote git failure is not treated as an unused version" "" "" 128 reject
check "GitHub release lookup failure is rejected" "" "" 2 reject list
check "GitHub commit lookup failure is rejected" 0.0.3 "" 0 reject api

for step in "Configure Maven Central credentials" "Import signing key" \
            "Check Maven Central requirements" "Publish to Maven Central" "Create GitHub Release"; do
  condition="$(awk -v step="$step" '$0 == "      - name: " step { getline; print; exit }' \
    .github/workflows/release.yml)"
  [[ "$condition" == "        if: steps.existing_release.outputs.exists != 'true'" ]] ||
    { echo "FAILED: $step must be skipped for an existing release." >&2; exit 1; }
done
echo "Passed: existing-release recovery cannot sign, publish, or recreate a release"

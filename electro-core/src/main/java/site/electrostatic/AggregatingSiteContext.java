package site.electrostatic;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AggregatingSiteContext implements SiteContext {

	private final PageContext page;
	private final SiteConfiguration site;

	@Override
	public String getLang() {
		return Optional.ofNullable(page.getLang()).orElse(site.getLang());
	}

}

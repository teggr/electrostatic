package electrostatic.utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ClasspathScanner implements AutoCloseable {

    private final ScanResult scanResult;

    public ClasspathScanner(String... packageNames) {

        log.info("scanning packages: {}", Stream.of(packageNames).collect(Collectors.joining(",")));

        scanResult = new ClassGraph()
                //.verbose()               // Log to stderr
                .enableAllInfo()         // Scan classes, methods, fields, annotations
                .acceptPackages(packageNames)     // Scan com.xyz and subpackages (omit to scan all packages)
                .scan();// Start the scan


    }

    public <T> List<String> findClassesImplementing(Class<T> clazz) {

        ClassInfoList listOfImplementingClassInfo = scanResult.getClassesImplementing(clazz);

        List<String> list = new ArrayList<>();

        for (ClassInfo implementingClassInfo : listOfImplementingClassInfo) {

            String implementingClassName = implementingClassInfo.getName();

            log.info("found {} implementing {}", implementingClassName, clazz.getName());

            list.add(implementingClassName);

        }

        return list;
    }

    @Override
    public void close() throws Exception {
        scanResult.close();
    }

}

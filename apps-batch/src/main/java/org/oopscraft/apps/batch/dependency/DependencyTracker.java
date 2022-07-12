package org.oopscraft.apps.batch.dependency;


import lombok.extern.slf4j.Slf4j;
import org.springframework.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class DependencyTracker {

    private Set<String> dependencyClasseNames = new HashSet<String>();

    private Set<String> dependencyPackageNames = new HashSet<String>();

    /**
     * Constructor
     */
    public DependencyTracker(String className) {
        visit(className, dependencyName -> {
			return true;
		});
    }

	/**
	 * Constructor
	 */
	public DependencyTracker(String className, Predicate<String> predicate) {
		visit(className, predicate);
	}

    /**
     * visit byte code
     *
     * @param className className
     */
    private void visit(String className, Predicate<String> predicate) {
        log.trace("== DependencyTracker.visit({})", className);
        try {
            // tracking dependency recursive
            ClassReader classReader = getClassReader(className);
            DependencyVisitor dependencyVisitor = new DependencyVisitor();
            classReader.accept(dependencyVisitor, 0);
            for (String element : dependencyVisitor.getClasses()) {
                String elementClassName = element.replaceAll("/", ".");
                String elementPackageName = elementClassName.substring(0, elementClassName.lastIndexOf('.'));

				// check predicate
				if(!predicate.test(elementClassName)){
					continue;
				}

				// adds package name
                if (!dependencyPackageNames.contains(elementPackageName)) {
                    dependencyPackageNames.add(elementPackageName);
                }

				// adds class name
                if (!dependencyClasseNames.contains(elementClassName)) {
                    dependencyClasseNames.add(elementClassName);
                    visit(elementClassName, predicate);
                }
            }
        } catch (Exception ignore) {
            log.warn(String.format("%s[%s]", ignore.getMessage(), className));
        }
    }

    /**
     * getClassReader
     * new ClassReader(className) not working.
     * ClassLoader.getSystemResourceAsStream is not working in spring boot jar
     * changes Thread.currentThread().getContextClassLoader()
     * @param className
     * @return
     * @throws IOException
     */
    private static ClassReader getClassReader(String className) throws IOException {
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(className.replace('.', '/') + ".class");
			byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(is);
            return new ClassReader(bytes);
        } catch (IOException e) {
            log.warn("== className[{}]:{}", className, e.getMessage());
            throw e;
        } finally{
            if(is != null) {
                try { is.close(); }catch(Exception ignore){};
            }
        }
    }

    /**
     * returns dependency class names
     *
     * @return
     */
    public Set<String> getDependencyClassNames() {
        return dependencyClasseNames.stream().collect(Collectors.toSet());
    }

    /**
     * returns dependency class names with predicates
     *
     * @return
     */
    public Set<String> getDependencyClassNames(Predicate<String> predicate) {
        return dependencyClasseNames.stream().filter(predicate).collect(Collectors.toSet());
    }

    /**
     * returns dependency package names
     *
     * @return
     */
    public Set<String> getDependencyPackageNames() {
        return dependencyPackageNames.stream().collect(Collectors.toSet());
    }

    /**
     * returns dependency package names with predicates
     *
     * @return
     */
    public Set<String> getDependencyPackageNames(Predicate<String> predicate) {
        return dependencyPackageNames.stream().filter(predicate).collect(Collectors.toSet());
    }



}

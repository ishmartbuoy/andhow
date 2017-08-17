package org.yarnandtail.andhow.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.tools.FileObject;
import org.yarnandtail.andhow.GlobalPropertyGroup;

/**
 *
 * @author ericeverman
 */
@SupportedAnnotationTypes("org.yarnandtail.andhow.GlobalPropertyGroup")
public class AndHowCompileProcessor extends AbstractProcessor {

	private static final String SERVICES_PACKAGE = "";
	private static final String RELATIVE_NAME = "META-INF/services/org.yarnandtail.andhow.PropertyGroup";
	
	public AndHowCompileProcessor() {
		//required by Processor API
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		
		Filer filer = this.processingEnv.getFiler();
		Set<? extends Element> selfRegistered = roundEnv.getElementsAnnotatedWith(GlobalPropertyGroup.class);
		

		
		
		if (selfRegistered != null && selfRegistered.size() > 0) {
			trace("Found " + selfRegistered.size() + " annotated elements");
			
			StringBuffer existingContent = new StringBuffer();
			StringBuffer newContent = new StringBuffer();

			try {

				FileObject groupFile = filer.getResource(
						javax.tools.StandardLocation.SOURCE_OUTPUT,
						SERVICES_PACKAGE, RELATIVE_NAME);

				if (groupFile != null) {
					existingContent.append(groupFile.getCharContent(true));
				}

			} catch (IOException ex) {
				//Ignore - This just means the file doesn't exist
			}
			
			
			
			for (Element e : selfRegistered) {
				
				String className = e.asType().toString();
				trace("Adding " + className + " to the list of PropertyGroups");
				
				newContent.append(className);
				newContent.append(System.lineSeparator());
			}
			
			if (existingContent.length() == 0) {
				trace("New " + RELATIVE_NAME + " file created");
				existingContent.append("# GENERATED BY THE AndHow AndHowCompileProcessor.");
			}

			try {

				FileObject groupFile = filer.createResource(
						javax.tools.StandardLocation.SOURCE_OUTPUT,
						SERVICES_PACKAGE, RELATIVE_NAME, selfRegistered.toArray(new Element[selfRegistered.size()]));

				try (Writer writer = groupFile.openWriter()) {
					writer.write(existingContent.toString());
					writer.write(System.lineSeparator());
					writer.write(newContent.toString());
				}

			} catch (IOException ex) {
				System.err.println("FAILED TO RUN COMPILE PROCESSOR");
				Logger.getLogger(AndHowCompileProcessor.class.getName()).log(Level.SEVERE, null, ex);
			}
			
		} else {
			trace("No annotated elements found");
		}

		return true;
		
	}
	
	public void trace(String msg) {
		System.out.println("AndHowCompileProcessor: " + msg);
	}

}
package it.cs.gssi.similaritymetamodels;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import it.cs.gssi.similaritymetamodels.EComparator;
import org.epsilon.ecl.tools.textcomparison.simmetrics.SimMetricsTool;


public class Main {

	
	
	public static void main(String[] args) throws Exception {
		// Create an instance of the generated Java class
				
		try {
			
		//File dir = new File("dataset");
		FileWriter writer = new FileWriter("models/similarity-matrix.csv");	
		
		String datasetfolder="models/";
		//write the CSV matrix 
		buildCSV(datasetfolder, writer);
		
				
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	public static void buildCSV(String datasetfolder, FileWriter writer) {
		
		File dir=new File(datasetfolder);
		String[] extensions = new String[] { "ecore" };
		try {
		ArrayList <String> header=new ArrayList<String>();
		
		header.add("Subject/dataset");
		
		
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		for (File file : files) {
			
			header.add(file.getName());
		}
		
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		
		List<IModel> models = new ArrayList<IModel>();
		String collect = header.stream().collect(Collectors.joining(","));
	    System.out.println(collect);
	    
	    
		writer.write(collect);
		
	    writer.write(System.getProperty( "line.separator" ));
			
		//for (File file : files) {
		for (int i=0; i<files.size();i++) {
		File file=files.get(i);
		//create similarity csv before edelta checker
		System.out.println("Processing "+i+"/"+files.size()+":"+file.getName());
		ArrayList<String> line=(similarity(datasetfolder+file.getName(), datasetfolder));
		String results = line.stream().collect(Collectors.joining(","));
		System.out.println(results);
		writer.write(results);
	    writer.write(System.getProperty( "line.separator" ));
	   
		
		
		}
		
		 writer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
		 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static ArrayList<String> similarity(String ecorefile, String datasetfolder) {
		
		ArrayList<String> line=new ArrayList<String>();
		try {
		File dir = new File(datasetfolder);
		String[] extensions = new String[] { "ecore" };
		
			//System.out.println("Getting all ecore in " + dir.getCanonicalPath()
				//	+ " including those in subdirectories");
		
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		String subject=ecorefile;
		
		line.add(subject);
		
		for (int i=0;i<files.size();i++) {
			File file=files.get(i);
			System.out.println("Comparing with:" +file.getName() + "("+i+"/"+files.size()+")");
			EComparator comparator=new EComparator(ecorefile, datasetfolder+file.getName());
			
			int simIndex=(comparator.execute(ecorefile, datasetfolder+file.getName()));
			//System.err.println(simIndex);
			line.add(String.valueOf(simIndex));
			//System.out.println("_____________________________________________");
		}
		
		//System.out.println(String.join(",", line));
		
		}catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
		}
		return line;
	}
	

protected static URI getFileURI(String fileName) throws URISyntaxException {
		
		//URI binUri = EpsilonStandaloneExample.class.
			//	getResource(fileName).toURI();
		URI binUri = new File(fileName).toURI();
			
		URI uri = null;
		
		if (binUri.toString().indexOf("bin") > -1) {
			uri = new URI(binUri.toString().replaceAll("bin", "src"));
		}
		else {
			uri = binUri;
		}
		
		return uri;
	}
	protected static EmfModel createEmfModel(String name, String model, 
			String metamodel, boolean readOnLoad, boolean storeOnDisposal) 
					throws EolModelLoadingException, URISyntaxException {
		EmfModel emfModel = new EmfModel();
		StringProperties properties = new StringProperties();
		properties.put(EmfModel.PROPERTY_NAME, name);
		properties.put(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI,
				getFileURI(metamodel).toString());
		properties.put(EmfModel.PROPERTY_MODEL_URI, 
				getFileURI(model).toString());
		properties.put(EmfModel.PROPERTY_READONLOAD, readOnLoad + "");
		properties.put(EmfModel.PROPERTY_STOREONDISPOSAL, 
				storeOnDisposal + "");
		emfModel.load(properties, (IRelativePathResolver) null);
		return emfModel;
	}
}

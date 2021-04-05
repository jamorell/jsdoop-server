package com.jsdoop.server.services.model_conversion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class ModelConverter {

	public static byte[] h5ToDL4j (byte[] h5Model) {
		try {
			System.out.println("received length " + h5Model.length);
			System.out.println("received " + h5Model);
			InputStream inputStream = new ByteArrayInputStream(h5Model);
			MultiLayerNetwork model = org.deeplearning4j.nn.modelimport.keras.KerasModelImport
					.importKerasSequentialModelAndWeights(inputStream, false);

			model.init();

			System.out.println("model summary " + model.summary());
			System.out.println("h5Model " + h5Model.toString());
			File temp = File.createTempFile("model", ".dl4j", null);
			model.save(temp);
			model = MultiLayerNetwork.load(temp, false);

			model.summary();
			
			byte[] dl4jBytes = FileUtils.readFileToByteArray(temp);
			return dl4jBytes;
//			return true;

		} catch (Exception e) { // (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static boolean h5ToJson(byte[] h5Model, String modelName) {

		try {
//			String modelName = "model_" + System.currentTimeMillis() + "_" + new Random().nextInt(999999) + ".h5" ;
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("/opt/files/topology/" + modelName + ".h5"));
			out.write(h5Model);
			out.flush();
	        out.close();
			
//	        ////////////////////////////
////	        String bundledScript = CharStreams.toString(
////	        	    new InputStreamReader(getClass().getResourceAsStream("/bundled_script_path.sh"), Charsets.UTF_8));
////	        	// Create a temp file with uuid appended to the name just to be safe
////	        	File tempFile = File.createTempFile("script_" + UUID.randomUUID().toString(), ".sh");
//	        final ClassLoader classLoader = ModelConverter.class.getClassLoader();
//	        final File file = new File(classLoader.getResource("conda_model_conversion.sh").getFile());
//	        System.out.println("file.exists sh  " + file.exists());
////	        final ProcessBuilder scriptBuilder = new ProcessBuilder();
////	        scriptBuilder.command(file.getPath());
//	        final ProcessBuilder scriptBuilder = new ProcessBuilder("bash", "-c", "bash " + file.getPath() + " " + modelName);
//	        
//	        
////	        scriptBuilder.command("bash", "-c", file.getPath() + " " + modelName);
//	        Process process = scriptBuilder.start();
//	        ///////////////////////
	        
	        
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", "bash /opt/files/conda_model_conversion.sh " + modelName);
			Process process = pb.start();
			boolean isAlive = process.isAlive();
			System.out.println("process " + isAlive);
			if (!isAlive) {
				System.out.println("process exitValue = " + process.exitValue());				
			} else {
				
				System.out.println("ERROR = " + IOUtils.toString(process.getErrorStream(), "UTF-8"));
			}
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			String output = "";
			while ((line = br.readLine()) != null) {
			  System.out.println("output = " + line);
			  output += line + "\n";
			  //return line;
			}
			
			File tempFile = new File("/opt/files/topology/" + modelName + ".h5");
			boolean deleted = tempFile.delete();
			System.out.println("temp file " + modelName + ".h5 deleted = " + deleted);
			if (output.isBlank()) {
				return false;
			} else {
				return true;
			}
			
			
			
		} catch (Exception e) { // (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
}

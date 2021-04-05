package com.jsdoop.server.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jsdoop.server.services.WeightsService;
import com.jsdoop.server.services.model_conversion.ModelConverter;

@RestController
public class ModelConversionController {

	private static final Logger log = LoggerFactory.getLogger(ModelConversionController.class);

	@Autowired
	private WeightsService weightsService;

	@PostMapping(path = "/testing_gradients", consumes = "multipart/form-data", produces = "text/plain")
//	public String testingApplyingWeights(@RequestPart("file")  Map<String, MultipartFile> files) {
	public String testingGradients(@RequestPart("files") MultipartFile[] files) {

		try {

			System.out.println("files.length " + files.length);
			for (int i = 0; i < files.length; i++) {
				byte[] bytes = files[i].getBytes();
				INDArray loadedweights = Nd4j.createNpyFromByteArray(bytes);
				System.out.println("loadedweights[" + i + "].shape = " + Arrays.toString(loadedweights.shape()));
			}
			return "OK";

		} catch (Exception e) { // (IOException e) {
			e.printStackTrace();
		}
		return "ERROR: No se pudo hacer el summary()";
	}


	@PostMapping(path = "/h5_to_dl4j_graph", consumes = "application/octet-stream", produces = "text/plain")
	public String convertH5toDL4JGraph(@RequestBody byte[] h5Model) {

		try {
			System.out.println("received length " + h5Model.length);
			System.out.println("received " + h5Model);
			InputStream inputStream = new ByteArrayInputStream(h5Model);
			ComputationGraph model = org.deeplearning4j.nn.modelimport.keras.KerasModelImport
					.importKerasModelAndWeights(inputStream, false);
			model.init();

			System.out.println("model summary " + model.summary());
			System.out.println("h5Model " + h5Model.toString());

			File temp = File.createTempFile("model", ".dl4j", null);
			model.save(temp);
			model = ComputationGraph.load(temp, false);

			return model.summary();

		} catch (Exception e) { // (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ERROR: No se pudo hacer el summary()";
	}

	@PostMapping(path = "/h5_to_dl4j_sequential", consumes = "application/octet-stream", produces = "text/plain")
	public String convertH5toDL4J(@RequestBody byte[] h5Model) {

		try {
			System.out.println("received length " + h5Model.length);
			System.out.println("received " + h5Model);
//			byte [] dl4jBytes = ModelConverter.h5ToDL4j(h5Model);
//			System.out.println("dl4jBytes length " + dl4jBytes.length);			
			
			InputStream inputStream = new ByteArrayInputStream(h5Model);
			MultiLayerNetwork model = org.deeplearning4j.nn.modelimport.keras.KerasModelImport
					.importKerasSequentialModelAndWeights(inputStream, false);

			model.init();

			System.out.println("model summary " + model.summary());
			System.out.println("h5Model " + h5Model.toString());
			File temp = File.createTempFile("model", ".dl4j", null);
			model.save(temp);
			model = MultiLayerNetwork.load(temp, false);

			return model.summary();

		} catch (Exception e) { // (IOException e) {
			e.printStackTrace();
		}
		return "ERROR: No se pudo hacer el summary()";
	}




	@PostMapping(path = "/h5_to_json", consumes = "application/octet-stream", produces = "text/plain")
	public String convertH5ToJson(@RequestBody byte[] h5Model, @RequestParam(name = "key") String key) {

		try {
			byte [] dl4jBytes = ModelConverter.h5ToDL4j(h5Model);
			System.out.println("dl4jBytes length " + dl4jBytes.length);	
			return "" + ModelConverter.h5ToJson(h5Model, key);
			
//			String modelName = "model_" + System.currentTimeMillis() + "_" + new Random().nextInt(999999) + ".h5" ;
//			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(modelName));
//			out.write(h5Model);
//			out.flush();
//	        out.close();
//			
//			System.out.println("received length " + h5Model.length);
//			System.out.println("received " + h5Model);
//			System.out.println("sending " + modelName);
//			ProcessBuilder pb = new ProcessBuilder("bash", "-c", "bash conda_model_conversion.sh " + modelName);
//			Process process = pb.start();
//			System.out.println("process " + process.isAlive());
//			InputStream is = process.getInputStream();
//			InputStreamReader isr = new InputStreamReader(is);
//			BufferedReader br = new BufferedReader(isr);
//			String line;
//			String output = "";
//			while ((line = br.readLine()) != null) {
//			  System.out.println("output = " + line);
//			  output += line + "\n";
//			  //return line;
//			}
//			return output;
			
		} catch (Exception e) { // (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "ERROR: No se pudo hacer el summary()";
	}
	
	
//	@PostMapping(path = "/h5_to_json", consumes = "application/octet-stream", produces = "text/plain")
//	public String convertH5ToJson(@RequestBody byte[] h5Model) {
//
////		try {
////			System.out.println("received length " + h5Model.length);
////			System.out.println("received " + h5Model);
////			ProcessBuilder pb = new ProcessBuilder("python","Your python file","" + h5Model);
////
////		} catch (Exception e) { // (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//        try {
//
////            String prg = "import sys\nprint int(sys.argv[1])+int(sys.argv[2])\n";
////            BufferedWriter out = new BufferedWriter(new FileWriter("sample_code.py"));
////            out.write(prg);
////            out.close();
//            long param1 = 10;
//            long param2 = 32;
//
////            ProcessBuilder pb = new ProcessBuilder("python", "model_conversion.py", "" + param2, "" + param1);
////            Process p = pb.start();
////            System.out.println("Process p = pb.start();");
////            System.out.println(p.isAlive());
////            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
////            System.out.println("BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));");
////            String line = in.readLine();
////            System.out.println("line = " + line);
////            int ret = new Integer(line).intValue();
////            System.out.println("value is : " + ret);
////            return "ret = " + ret;
//            
////            ProcessBuilder pb = new ProcessBuilder("python", "model_conversion.py", "" + param2, "" + param1);
////            ProcessBuilder pb = new ProcessBuilder("bash", "conda_model_conversion.sh", "-c", "" + param2, "" + param1);
//            
//            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "bash conda_model_conversion.sh " + param2 + " " + param1);
////            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "ls ." );
//            Process process = pb.start();
//            System.out.println("process " + process.isAlive());
//            InputStream is = process.getInputStream();
//            InputStreamReader isr = new InputStreamReader(is);
//            BufferedReader br = new BufferedReader(isr);
//            String line;
//            
//            long[] args = {param2, param1};
//            System.out.printf("Output of running %s is:", Arrays.toString(args));
//
//            while ((line = br.readLine()) != null) {
//              System.out.println("line = " + line);
//            }
//            
//            return "";
//
//            
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//		
//		return "ERROR: No se pudo hacer el summary()";
//	}
	

	
////	@PostMapping(path = "/h5_to_dl4j", consumes = "application/json", produces = "application/json")
//	@PostMapping(path = "/h5_to_dl4j_sequential", consumes = "application/octet-stream", produces = "text/plain")
//	public String converH5toDL4J(@RequestBody byte[] h5Model) {
//
//
//		try {
//			System.out.println("received length " + h5Model.length);
//			System.out.println("received " + h5Model);
//			InputStream inputStream = new ByteArrayInputStream(h5Model);
//	    	MultiLayerNetwork model = org.deeplearning4j.nn.modelimport.keras.KerasModelImport.importKerasSequentialModelAndWeights(inputStream, false);
//
//	    	model.init();
//			
////	        int nIn = 1; // 4
////	        int nMiddle = 10; //3
////	        int nOut = 1; // 3		
////	        Nd4j.getRandom().setSeed(12345);
////	        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
////	                .seed(12345)
//////	                .activation(Activation.TANH)
//////	                .activation(Activation.SIGMOID)
////	                .activation(Activation.SIGMOID)
////	               
////	                .weightInit(WeightInit.XAVIER)
//////	                .updater(new Nadam())
////	                .updater(new RmsProp())
//////	                .updater(new Sgd())
////	                .list()
////	                .layer(new DenseLayer.Builder().nIn(nIn).nOut(nMiddle).build())
////	                .layer(new DenseLayer.Builder().nIn(nMiddle).nOut(nMiddle).build())
//////	                .layer(new DenseLayer.Builder().nIn(nMiddle).nOut(nMiddle).build())
////	                .layer(new DenseLayer.Builder().nIn(nMiddle).nOut(nOut).build())
////	                .build();
//
////	        System.out.println("before model");
////	        MultiLayerNetwork model = new MultiLayerNetwork(conf);		
////	        System.out.println("model " + model);	        
////	        System.out.println("after model");			
////	        model.init();
//
//	        System.out.println("model summary " + model.summary());
//	        System.out.println("h5Model " + h5Model.toString());
//	        return model.summary();
////			File temp = File.createTempFile("model", ".dl4j", null);
////	        model.save(temp);
////	        model.load(temp, false);
//	        
//
//		} catch (Exception e) { // (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return "ERROR: No se pudo hacer el summary()";
////		return new LimitConfiguration(configuration.getMaximum(), configuration.getMinimum());
//	}

//	@GetMapping("/h5_to_dl4j")
//	public String retriveLimitsFromConfigurations(Object h5Model) {
//
//
//		try {
////			InputStream dataReceived = response.body().byteStream();
////	    	MultiLayerNetwork model = org.deeplearning4j.nn.modelimport.keras.KerasModelImport.importKerasSequentialModelAndWeights(dataReceived, false);
//			
//			
//	        int nIn = 1; // 4
//	        int nMiddle = 10; //3
//	        int nOut = 1; // 3		
//	        Nd4j.getRandom().setSeed(12345);
//	        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
//	                .seed(12345)
////	                .activation(Activation.TANH)
////	                .activation(Activation.SIGMOID)
//	                .activation(Activation.SIGMOID)
//	               
//	                .weightInit(WeightInit.XAVIER)
////	                .updater(new Nadam())
//	                .updater(new RmsProp())
////	                .updater(new Sgd())
//	                .list()
//	                .layer(new DenseLayer.Builder().nIn(nIn).nOut(nMiddle).build())
//	                .layer(new DenseLayer.Builder().nIn(nMiddle).nOut(nMiddle).build())
////	                .layer(new DenseLayer.Builder().nIn(nMiddle).nOut(nMiddle).build())
//	                .layer(new DenseLayer.Builder().nIn(nMiddle).nOut(nOut).build())
//	                .build();
//
//	        System.out.println("before model");
//	        MultiLayerNetwork model = new MultiLayerNetwork(conf);		
//	        System.out.println("model " + model);	        
//	        System.out.println("after model");			
//	        model.init();
//
//	        System.out.println("model summary " + model.summary());
//	        System.out.println("h5Model " + h5Model.toString());
//	        return model.summary();
////			File temp = File.createTempFile("model", ".dl4j", null);
////	        model.save(temp);
////	        model.load(temp, false);
//	        
//
//		} catch (Exception e) { // (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return "ERROR: No se pudo hacer el summary()";
////		return new LimitConfiguration(configuration.getMaximum(), configuration.getMinimum());
//	}
}
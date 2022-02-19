package com.example.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.ResourceUtils;

import com.example.test.utils.JsonUtils;
import com.example.test.utils.Validators;

import lombok.extern.slf4j.Slf4j;

//@SpringBootApplication
@Slf4j
@Configuration
@ComponentScan("com.example.test")
public class TestApplication {
	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private JsonUtils utils;
	

	public TestApplication( JsonUtils utils) {
		super();
		this.utils = utils;
	}
	private static String FILE_NAME = "src/main/resources/output/";
	
	public static void main(String[] args)  {
		Integer option=-1;
		//SpringApplication.run(TestApplication.class, args);
		System.out.println("Please select config file as following");
		TestApplication obj = new TestApplication(new JsonUtils());
		try {
			Resource[] resourceArr=obj.loadResources("configs/*");
			List<Resource>resources=Arrays.asList(resourceArr);
			IntStream.range(0, resources.size())
			.forEach(idx->{
				//log.info("file name:{}",r.getFile().getName());
				try {
					System.out.println("press "+idx+" to use config as : "+resources.get(idx).getFile().getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();
			try
			{
				if(input!=null && !input.equals(""))
				option=Integer.parseInt(input.trim());
				else
					System.out.println("Wrong option selected");
			}
			catch (Exception e) {
				//e.printStackTrace();
				System.out.println("Wrong option selected");
			}
			
			if(Validators.isValidInput(option, resources.size()) )
			{
				System.out.println((option > -1 && option <resources.size()?"Selected config file is: "+option+" i.e. "+resources.get(option).getFile().getName() :"wrong input"));
				String path="classpath:configs/"+resources.get(option).getFile().getName();
				System.out.println("file path: "+path);  
				File file = ResourceUtils.getFile(
						path);
					    String json = new String(
					      Files.readAllBytes(file.toPath()));
			    JSONObject jsonObj = new JSONObject(json);
			    JSONArray inputFiles=jsonObj.getJSONArray("inputFile");
			    FILE_NAME+=jsonObj.getString("outputFile");
			    JSONArray joinconditions=jsonObj.getJSONArray("joinCondition");
			    List<String> joinConditionList=new ArrayList<>();
				// get all join conditions
			    for (int j = 0; j < joinconditions.length(); j++) 
				{
					JSONObject joinCondition=new JSONObject(joinconditions.get(j).toString());
					joinCondition.keys().forEachRemaining(conditionKey->{
						joinConditionList.add(conditionKey.toString());
					});
				}
			   // log.info("join condition list: {}",joinConditionList);
			    JSONArray mergedJson= new JSONArray();
			    for (int i = 0; i < inputFiles.length(); i++) 
			    {
			    	String filename = inputFiles.getString(i).toString();
			    	System.out.println("reading file: "+filename);
			    	System.out.println(" ");
					path="classpath:data/"+filename;
					file = ResourceUtils.getFile(
							path);
					json = new String(
						      Files.readAllBytes(file.toPath()));
					//System.out.println(json);
					JSONArray arr=new JSONArray(json);
					// remove keys that are not in join conditions
						 for (int k = 0; k < arr.length(); k++) 
						    {
						    	JSONObject dataObject=new JSONObject(arr.get(k).toString());
						    	dataObject.keySet().removeIf(jsonKey -> !joinConditionList.contains(jsonKey));
						    	mergedJson.put(dataObject);
						    }		 
			    }
			    System.out.println("afterRemoval :");
			    System.out.println(mergedJson);
			    System.out.println("Writing to file : "+FILE_NAME+" please refresh resources folder before opening of files");
//			    FileUtils.touch(new File(FILE_NAME));
//			    FileWriter f=new FileWriter(FILE_NAME);
//			    f.write(mergedJson.toJSONObject(mergedJson));
			    try (FileWriter fw = new FileWriter(FILE_NAME)) {
			    	fw.write(mergedJson.toString()); 
			    	fw.flush();
		 
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
			else
			{
				System.out.println("option: "+option+" is not valid");
			}
			scanner.close();
		} catch (IOException e) {
			System.out.println("exception occured "+e.getMessage());
			e.printStackTrace();
		}

	}

	Resource[] loadResources(String pattern) throws IOException {
		return ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern);
	}

}

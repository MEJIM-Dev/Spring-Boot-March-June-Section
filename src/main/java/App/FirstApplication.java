package App;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class FirstApplication {

	public static void main(String[] args) {
//		System.out.println(JsonPage());
		SpringApplication.run(FirstApplication.class, args);
	}

	public static void testing (String password){
		System.out.println(password);
	}

//	public static List<Person> JsonPage(){
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			InputStream inputStream = new FileInputStream(new File("C:\\Users\\ejimm\\Desktop\\Javascript\\March-june\\Node\\data.json"));
//			TypeReference<List<Person>> typeReference = new TypeReference<List<Person>>() {} ;
//			List<Person> liPer = mapper.readValue(inputStream, typeReference);
//
////			System.out.println(liPer);
//			List<String> data = new ArrayList<>();
//			for (Person p: liPer ) {
////				System.out.println("Name: "+p.getName());
////				System.out.println("No: "+p.getNo());
//				data.add(p.toString());
//			}
//
//			inputStream.close();
//
////			data.forEach(s -> System.out.println(s));
//			return liPer;
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (StreamReadException e) {
//			e.printStackTrace();
//		} catch (DatabindException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		List<Person> personError = new ArrayList<>();
//		Person error = new Person();
//		error.setName("Buffer reader error");
//		error.setNo("404");
//		personError.add(error);
//		return  personError;
//	}

}

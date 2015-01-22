package Server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PHP {

	public static String execPHP(String scriptName) {
		StringBuilder output = new StringBuilder();
		BufferedReader input = null;
		String phpPath = "/usr/bin/php";
		try {
			String line;
			Process p = Runtime.getRuntime().exec(phpPath + " " +scriptName);
			input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				output.append(line+"\n");
			}
			
			if(line == null){
				p.destroy();
			}
		} catch (Exception err) {
			err.printStackTrace();
		}finally{
			if(input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return output.toString();
	}

	
}

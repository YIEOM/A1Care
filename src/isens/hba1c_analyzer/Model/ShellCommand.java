package isens.hba1c_analyzer.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellCommand {

	Process shell;
	
	public void setCommand(String cmd) {
		
		try {
			
			shell = Runtime.getRuntime().exec(cmd);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedReader getMessage() {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(shell.getInputStream()));
		
		return br;
	}
}

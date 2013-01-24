package com.bremsstrahlung.orion.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL20;

public abstract class AbstractShader {
	protected int programId = -1;

	public AbstractShader() {
	}
	
	protected int load(String file, int type) throws Exception {
		int shaderId = 0;
		
		StringBuilder source = new StringBuilder();
		
		
		try {
			BufferedReader rdr = new BufferedReader(new FileReader(file));
			
			try {
				String line;
				while((line = rdr.readLine()) != null) {
					source.append(line);
				}
			}
			catch(IOException e) {
				throw e;
			}
			finally {
				rdr.close();
			}
		}
		catch(IOException e) {
			throw e;
		}
		
		shaderId = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderId, source.toString());
		GL20.glCompileShader(shaderId);
		
		String s = GL20.glGetShaderInfoLog(shaderId, 1000);
		if(!s.isEmpty()) {
			throw new Exception(s);
		}
		
		return shaderId;
	}
	
	protected void link(int... shaders) {
		programId = GL20.glCreateProgram();
		
		for(int shader: shaders) {
			GL20.glAttachShader(programId, shader);
			
			GL20.glDeleteShader(shader);
		}
		
		GL20.glLinkProgram(programId);
	}
	
	public abstract void use();
	
	public void delete() {
		GL20.glDeleteProgram(programId);
	}
}

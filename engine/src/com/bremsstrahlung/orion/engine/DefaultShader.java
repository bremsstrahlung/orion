package com.bremsstrahlung.orion.engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

public class DefaultShader extends AbstractShader {
	private FloatBuffer mvpMatrix = BufferUtils.createFloatBuffer(16);
	private int mvpMatrixLoc = -1;
	
	public DefaultShader() {
		try {
			int v = load("assets/shaders/default/vertex.glsl", GL20.GL_VERTEX_SHADER);
			int f = load("assets/shaders/default/fragment.glsl", GL20.GL_FRAGMENT_SHADER);

			link(v, f);
		}
		catch(Exception e) {
			System.err.println(e);
		}
	}
	
	public void setMvpMatrix(Matrix4f mvpMatrix) {
		mvpMatrix.store(this.mvpMatrix);
	}
	
	public void use() {
		GL20.glUseProgram(programId);
		
		if(mvpMatrixLoc < 0) {
			mvpMatrixLoc = GL20.glGetUniformLocation(programId, "in_mvpMatrix");
		}
		
		GL20.glUniformMatrix4(mvpMatrixLoc, true, mvpMatrix);
	}
}

package com.gerken.gumbo.monitor.contract.cargo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class GumboCargo {

	public GumboCargo() {

	}

	public GumboCargo(byte[] bytes) throws Exception {
		fromBytes(bytes);
	}
	
	public byte[] asBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		writeObject(oos);
		return baos.toByteArray();
	}
	
	protected void fromBytes(byte[] bytes) throws ClassNotFoundException, IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		readObject(ois);
	}

	protected abstract void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException;

	protected abstract void writeObject(ObjectOutputStream out) throws IOException;
	
}

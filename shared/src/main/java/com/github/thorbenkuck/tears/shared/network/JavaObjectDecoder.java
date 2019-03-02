package com.github.thorbenkuck.tears.shared.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

public class JavaObjectDecoder implements ObjectDecoder {
	@Override
	public Object apply(byte[] bytes) {
		try (ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
			 ObjectInput objectInput = new ObjectInputStream(byteInput)) {
			Object o = objectInput.readObject();
			return o;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}

package com.hmg.json2java.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
//This is object serialization. Object serialization is not applicable for saving .class byte code
public class ObjectFileWriter {
	public static void writeObjectToFile(String filepath, Object obj) throws ClassNotFoundException {
		try {
			String fileName=System.getProperty("user.dir")+"\\";
			System.out.println("file:"+fileName);
			File f=new File(fileName);
			if(!f.exists()) {
				f.mkdirs();				
			}			
			//TODO:MKDIR UNTIL CLASS
			fileName+=filepath.replace('.', '\\')+".class";
            OutputStream file = new FileOutputStream(fileName);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);
            try {
            	Class cc=JavaSourceToObject.dynamicClassLoader.loadClass(filepath);
//                Class cc = Class.forName(filepath);
                System.out.println("class is writing "+cc);
                
                output.writeObject(cc);
            }catch (Exception e) {
				e.printStackTrace();
			} finally {
                output.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	
	public static void readObjectToFile(String filepath) throws ClassNotFoundException {
		try {
			
		
			
			String fileName=System.getProperty("user.dir")+"\\"+filepath.replace('.', '\\')+".class";
			//
			Class clazz = JavaSourceToObject.dynamicClassLoader.loadClass(filepath);

			if(clazz!=null)
			{
				System.out.println("Read "+clazz);
				return;
			}
			File f=new File(fileName);
			FileReader fr=new FileReader(f);
			
//			System.out.println(f.exists());
//            // use buffering
//            InputStream file = new FileInputStream(fileName);
//            InputStream buffer = new BufferedInputStream(file);
//            ObjectInput input = new ObjectInputStream(buffer);
            try {
                // deserialize the class
            	ArrayList<Byte> intArr=new ArrayList<Byte>();
            	
                int read;
				do{
                	 read=fr.read();
                	 intArr.add((byte)read);                	 
                 }while(read!=-1);
				byte[] bytes=new byte[intArr.size()];
				int i=0;
				for (Byte b : intArr) {
					bytes[i]=b;
					i++;
				}
                // display 
				 ByteArrayInputStream in = new ByteArrayInputStream(bytes);

				    
               
            }catch (Exception e) {
            	e.printStackTrace();
			}
            
            finally {
//                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}

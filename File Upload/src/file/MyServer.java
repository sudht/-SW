package file;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import file.FileDAO;

public class MyServer {
	public ServerSocket serv;
	public Socket sock;

	BufferedOutputStream bos = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;

	int fileTransferCount = 0;
	long fileTransferSize = 0;

	File copyFile = null;

	public static void main(String []  args) {
		new MyServer(5000);
	}

	public MyServer(int port) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		try {
			this.serv = new ServerSocket(port);
			System.out.println("Server Started...");

			while(true) {
				this.sock = this.serv.accept();

				try {
					System.out.println("Client Accept...!!");

					this.dis = new DataInputStream(this.sock.getInputStream());
					this.dos = new DataOutputStream(this.sock.getOutputStream());

					try {
						this.fileTransferCount = dis.readInt();
						for(int i=0; i<fileTransferCount; i++) {
							Date d = new Date();
							String filename = dis.readUTF();
							this.copyFile = new File("C:/JSP/graph/" + sdf.format(d) + "_" + filename);
							this.fileTransferSize = dis.readLong();
							this.bos = new BufferedOutputStream(new FileOutputStream(this.copyFile, false));
							new FileDAO().upload(filename, sdf.format(d));

							int bufSize = 1024;
							long count = 0;
							int totalReadCount = (int)this.fileTransferSize / bufSize;
							int lastReadSize = (int)this.fileTransferSize % bufSize;
							byte[] buffer = new byte[bufSize];

							int readBufLength = 0;
							for(int k = 0; k<totalReadCount; k++) {
								if((readBufLength = dis.read(buffer, 0, bufSize)) != -1) {
									bos.write(buffer, 0, readBufLength);
									count += readBufLength;
								}
							}
							if(lastReadSize > 0) {
								if((readBufLength = dis.read(buffer, 0, lastReadSize)) != -1) {
									bos.write(buffer,  0,  readBufLength);
									count += readBufLength;
								}
							}
							dos.writeLong(count);
							dos.writeUTF(this.copyFile.getName() + " Transfer exit");

							if(this.bos != null) this.bos.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if(this.dis != null) this.dis.close();
						if(this.dos != null) this.dos.close();
						if(this.sock != null) this.sock.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package org.linuxkernel.proof.digger.datasource;

public class DataSourceThread implements Runnable {
	private DataSource dataSource;

	public DataSourceThread(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void run() {

		dataSource.getIssue("北京大学校长是谁？");
	}

	public static void main(String[] args) {
		DataSource dataSource1 = new GoogleDataSource();
		DataSource dataSource2 = new BaiduDataSource();
		Thread thread1 = new Thread(new DataSourceThread(dataSource1));
		Thread thread2 = new Thread(new DataSourceThread(dataSource2));
		thread1.start();
//		thread2.start();
	}
}

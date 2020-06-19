package com.amazonaws.lambda.demo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBSnapshot;
import com.amazonaws.services.rds.model.DeleteDBSnapshotRequest;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsResult;
import com.amazonaws.services.rds.model.ListTagsForResourceRequest;
import com.amazonaws.services.rds.model.ListTagsForResourceResult;
import com.amazonaws.services.rds.model.Tag;

public class testt {
	private static final String df = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat dateFormat = new SimpleDateFormat(df);
    private static final DateTimeFormatter dateFormat8 = DateTimeFormatter.ofPattern(df);
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AWSCredentials credentials = new BasicAWSCredentials(
				 "AWS_key", 
				  "AWS SECRET KEY"
				);
		AmazonRDSClient client = new AmazonRDSClient(credentials);
	 
		DescribeDBSnapshotsResult d= client.describeDBSnapshots();
		List<DBSnapshot>	dbSnapList=	d.getDBSnapshots();
	 
	 
		for(DBSnapshot dbsnap:dbSnapList) {
			ListTagsForResourceRequest request = new ListTagsForResourceRequest().withResourceName(dbsnap.getDBSnapshotArn());
			ListTagsForResourceResult response = client.listTagsForResource(request);
			System.out.println(dbsnap.getInstanceCreateTime());
			 
			List<Tag> test = response.getTagList();
			for(Tag tags : test) {
				Tag tag = tags.withKey("env") ;
				 Date lambdaDate = dbsnap.getInstanceCreateTime();
				 Date newOne = new testt().newDate(lambdaDate,Integer.parseInt(tag.getValue()));
				 if(new Date().compareTo(newOne)>0) {
					 System.out.println("delete");
					 DeleteDBSnapshotRequest deleteRequest = new DeleteDBSnapshotRequest().withDBSnapshotIdentifier(dbsnap.getDBSnapshotIdentifier());
					 DBSnapshot deleteResponse = client.deleteDBSnapshot(deleteRequest);
					 System.out.println(deleteResponse.getStatus());
					 if(deleteResponse.getStatus().equals("deleted")) {
						 System.out.println(deleteResponse.getStatus()+"success");
					 }
				 }
			}
		}
//		DescribeDBClustersRequest request = new DescribeDBClustersRequest().withDBClusterIdentifier("database-1");
//		DescribeDBClustersResult response = client.describeDBClusters(request);
//		System.out.println(response.);
	}

	private Date newDate(Date currentDate,int days) {
		

		 
	        System.out.println("date : " + dateFormat.format(currentDate));

	        // convert date to localdatetime
	        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	        System.out.println("localDateTime : " + dateFormat8.format(localDateTime));

	        // plus one
	        localDateTime = localDateTime.plusDays(days);
	      //  localDateTime = localDateTime.plusHours(1).plusMinutes(2).minusMinutes(1).plusSeconds(1);

	        // convert LocalDateTime to date
	        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

	}
}

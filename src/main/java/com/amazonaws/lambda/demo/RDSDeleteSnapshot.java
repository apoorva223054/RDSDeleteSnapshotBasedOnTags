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
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBSnapshot;
import com.amazonaws.services.rds.model.DeleteDBSnapshotRequest;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsResult;
import com.amazonaws.services.rds.model.ListTagsForResourceRequest;
import com.amazonaws.services.rds.model.ListTagsForResourceResult;
import com.amazonaws.services.rds.model.Tag;

public class RDSDeleteSnapshot implements RequestHandler<Object, String> {

	private static final String df = "yyyy-MM-dd HH:mm:ss";
    private static final DateFormat dateFormat = new SimpleDateFormat(df);
    private static final DateTimeFormatter dateFormat8 = DateTimeFormatter.ofPattern(df);
	public static void main(String[] args) {}

	

    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input:- " + input);



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
			 
			 
			List<Tag> test = response.getTagList();
			for(Tag tags : test) {
				Tag tag = tags.withKey("env") ;
				 Date lambdaDate = dbsnap.getInstanceCreateTime();
				 Date newOne = new RDSDeleteSnapshot().newDate(lambdaDate,Integer.parseInt(tag.getValue()));
				 if(new Date().compareTo(newOne)>0) {
					 
					 DeleteDBSnapshotRequest deleteRequest = new DeleteDBSnapshotRequest().withDBSnapshotIdentifier(dbsnap.getDBSnapshotIdentifier());
					 DBSnapshot deleteResponse = client.deleteDBSnapshot(deleteRequest);
					 if(deleteResponse.getStatus().equals("deleted")) {
						 return deleteResponse.getStatus();
					 }
				 }
			}
		}
//		DescribeDBClustersRequest request = new DescribeDBClustersRequest().withDBClusterIdentifier("database-1");
//		DescribeDBClustersResult response = client.describeDBClusters(request);
//		System.out.println(response.);
	
	
        return "fail";
    }
    private Date newDate(Date currentDate,int days) {
		
         LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
         localDateTime = localDateTime.plusDays(days);
         return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

}
}

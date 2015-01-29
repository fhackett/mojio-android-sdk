package com.TTT.MojioSDKSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.TTT.MojioSDKSource.Models.Vehicle;
import com.TTT.MojioSDKSource.OauthHelper.OauthHelper;
import com.TTT.MojioSDKSource.Requests.LocationRequest;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;



public class MojioSDKSource{
	
	
	private SpiceManager _contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);
	private String _lastRequestCacheKey = null;
	
	public static void RequestAccessToken( Activity context){
		
		Intent i = new Intent(context, MojioSDKActivity.class);
		context.startActivityForResult(i, 22);
			

	}
	
	
	public static void GetElement(String url){
		new GetElementTask(url).execute();
	}
	
	private static class GetElementTask extends AsyncTask<Void, Void, Void>{
		
		String _url;
		
		public GetElementTask(String url){
			_url = url;
			
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {

				URL productUrl = new URL(_url);
				HttpsURLConnection conn = (HttpsURLConnection) productUrl.openConnection();

				
				// Build JSON object from HTTP response.
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = rd.readLine()) != null)
					sb.append(line);

				rd.close();
				String jsonData = sb.toString();
//				new ObjectMapper().readValue(jsonData, Vehicle.class);
				
				Log.e("TESTING", "THIS IS WHAT I GOT: " + jsonData);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(Exception e){}
			
			return null;
		}
		
	}
	
	public static void getVehicle(Context context){
		
		new GetVehicle(context).execute();
		
		
	}


	
	private static class GetVehicle extends AsyncTask<Void, Void, String>{
		
		Context _context;
		
		public GetVehicle(Context context){
			_context = context;
		}

		@Override
		protected String doInBackground(Void... params) {
			OauthHelper oauthHelper = new OauthHelper(_context);
			LocationRequest lq = new LocationRequest("https://api.moj.io/v1/Vehicles/53cdeca5-b268-4a25-bfde-3938b5cf7d47", oauthHelper);
			try {
				Vehicle test = lq.loadDataFromNetwork();
				Log.e("TESTING LOCATION", "THE LONG IS: " + test.getFuelLevel());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {

		}
		
		
	}



}

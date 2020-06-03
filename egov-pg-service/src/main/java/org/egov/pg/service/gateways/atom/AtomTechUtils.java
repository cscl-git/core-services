package org.egov.pg.service.gateways.atom;

public class AtomTechUtils {

	/*
	 * Hashing using key with HMACSHA512
	 */
	public static byte[] encodeWithHMACSHA2(String text,String keyString) throws java.security.NoSuchAlgorithmException, java.security.InvalidKeyException, java.io.UnsupportedEncodingException
	{
		
		java.security.Key sk = new javax.crypto.spec.SecretKeySpec(keyString.getBytes("UTF-8"),"HMACSHA512");
		javax.crypto.Mac mac = javax.crypto.Mac.getInstance(sk.getAlgorithm());
		mac.init(sk);
			
		byte[] hmac = mac.doFinal(text.getBytes("UTF-8"));
			
		return hmac;
	}
	
	/*
	 * Convert from byte array to HexString
	 */
	public static String byteToHexString(byte byData[])
	{
		StringBuilder sb = new StringBuilder(byData.length * 2);
		
		for(int i = 0; i < byData.length; i++)
		{
			int v = byData[i] & 0xff;
			if(v < 16)
				sb.append('0');
			sb.append(Integer.toHexString(v));
		}
		
		return sb.toString();
	}
	
	
	/*
	 * Encoded with HMACSHA512 and encoded with utf-8 using url encoder for given list of parameter values appended with the key 
	 */
	public static String getEncodedValueWithSha2(String hashKey,String ...param)
	{
		String resp = null;
			
		StringBuilder sb = new StringBuilder();
		for (String s : param) {
			sb.append(s);
		}
		
		try{
			System.out.println("[getEncodedValueWithSha2]String to Encode =" + sb.toString());
			resp = byteToHexString(encodeWithHMACSHA2(sb.toString(), hashKey));
			//resp = URLEncoder.encode(resp,"UTF-8");
			
		}catch(Exception e)
		{
			System.out.println("[getEncodedValueWithSha2]Unable to encocd value with key :" + hashKey + " and input :" + sb.toString());
			e.printStackTrace();
		}
		
		return resp;
	}
	
	private static void _main(String[] args) {
		
		String login = "7";
		String pass = "Test@123";
		String ttype = "NBFundTransfer";
		String prodid = "NSE";
		String txnid = "Mer123";
		String amt = "3000.00";
		String txncurr = "INR";
		String reqHashKey = "KEY123657234";
		//login,pass,ttype,prodid,txnid,amt,txncurr
		String signature_request = getEncodedValueWithSha2(reqHashKey, login,pass,ttype,prodid,txnid,amt,txncurr);
		System.out.println("Request signature ::" + signature_request);
		
		
		//Response signature based on parameters
		
		String mmp_txn = "121212";
		String mer_txn = "Mer123";
		String f_code = "Ok";
		String prod = "NSE";
		String discriminator = "NB";
		amt = "3000.00";
		String bank_txn = "Bank123";
		String respHashKey = "KEYRESP123657234";
		//mmp_txn,mer_txn, f_code, prod,  discriminator, amt, bank_txn		
		String signature_response = getEncodedValueWithSha2(respHashKey, mmp_txn,mer_txn, f_code,prod, discriminator, amt, bank_txn);
		System.out.println("Response signature ::" + signature_response);
	}
}

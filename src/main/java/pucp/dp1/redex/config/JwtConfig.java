//package pucp.dp1.redex.config;
//
//public class JwtConfig {
//	
//public static final String LLAVE_SECRETA = "alguna.clave.secreta.123";
//	
//	public static final String RSA_PRIVADA = "-----BEGIN RSA PRIVATE KEY-----\r\n" + 
//			"MIIEowIBAAKCAQEAqIiaODnBV2Fex1aohG55ua6izlxdY/KXUkYabPTp10AvsT66\r\n" + 
//			"gshG93kmmmC9qBR2d73QrjaQxyVl0Cell4d1gnoOJMzhWq2vpXYj2vJJPrFocurZ\r\n" + 
//			"TMh5F2PcRZLB5xK0whQEaod/jdDH4NryCf3KqSk7nmHE2wcj3nXpMBLPCX5kyzvJ\r\n" + 
//			"X+Mg1ErX//8/HuhhcQFJ/rI2/OTOPMtqg0x4HQaNP7mBFaS3P2l5vFmu+us07prd\r\n" + 
//			"2QlfIKhyiLG+IJwrO2c1km2FyWFs+bLmLZe2BbBQDIUviN2LNOkkvwN5YtGkEbkm\r\n" + 
//			"XjNw3Vgx+aAdOU0YaSa6N47DEYHKGLsCwRXZwwIDAQABAoIBACG75hKnDNoutUf1\r\n" + 
//			"QpnvexH9sLzZpB/jaQKg4Z05kKzzPhieU7rl176lU2LHRm8kWWoyZljYtzYFeFGn\r\n" + 
//			"/e9Mv+qz/6zCDcGWzneyJA8pPBv+asfF1mUWI5bY+Dg83e8gwWmvlAq2wINkbP3H\r\n" + 
//			"FXeGg1BE3pgfPcYRsc5Pj7N9sE9X2Byzvr2CQV+//hnodr4EJ/cOC5ZWGqroDUZ9\r\n" + 
//			"VFvoy7b/WwwqP59k7iqcVeqvLUTlvg9RFcdLa6nF/Kks9QZksIZgmsU8u7QPRJPc\r\n" + 
//			"XpuK2RF2FuARo/ilG9gWhX25JvMJ41dshg6fmR+VXBGblOFsUh2umBmkoTUhM9P1\r\n" + 
//			"LrkSNoECgYEA2Sejnzi9aoyPM49F6XebZgxUcqC2RANOv6bGIK0waOuMCK/WiRhT\r\n" + 
//			"6Sm8mFnr1wPSCU4AaRE2BlDsJ9zgWPHN8RCIT9DjC/IN4yVXIyumj12iaqeDj10r\r\n" + 
//			"8tdKFlcFGOi9OMmpIr7y2Xt904jCzYCtoGHllu8WI8u48zafHWESMGECgYEAxq5o\r\n" + 
//			"mPmAXL6XvMIXWJJI7FfLgbAmKQ0gVoIkdQTOb3I1guSinKBfpSaazI+40mHnlU0E\r\n" + 
//			"XPbzaUJL4DZ2D3Ijc5lr7kSJU2F5iqCL9H/xS5K4m7PCb0yTQlHJuYKtiCrA9vwI\r\n" + 
//			"ucEZLPZknwRn8AnIV7YOtwIfz8DC2VvJLWuMjKMCgYAbr/Yowu9DupA3nrjnP5zM\r\n" + 
//			"sh3CT8NtgI512SnB2NMnTYEZSnIsILHyNKQbOLvdOlt5XDypKMyemYrCumLQdGQx\r\n" + 
//			"rE30/0kzqLUklQo7LcGa3fFrb9hSWyrTV6fWmVnJB0O5Dd1G1NnZ6OjuI63SzelV\r\n" + 
//			"XaYMTIeiIMOAHyLCeDH9gQKBgGn+U7kAqnVQDL1bwn6zFApaUCxw3/rYkN718SMo\r\n" + 
//			"nEUiZd/dv87VVBFURhNJvW0nZBfnjaNW78bA4p/MDjHxT251c/oaQDTmdKP7QI8A\r\n" + 
//			"zqblsG0IentMG+euoHLCf3d8b5OUI8eNd7PgAxA+loCdh28bpkAF9xirmofBCtOb\r\n" + 
//			"Y9EHAoGBAMikmj03qpES5ZZG4ja/fdA/ySAk7nqr2wZ3fQ8n4lUW/8jbcLwDgBdE\r\n" + 
//			"Z5N2SO2UBoqQCF2mefsQxKdXuFg2pNiuPk+ZL0fcpFKW9mHYWnS4wmMhq4S3OQoh\r\n" + 
//			"s437GIv6FcwlNiDlF0QpjFDYpkm4AtV2oApXAhEjhGZy7pkTYLT8\r\n" + 
//			"-----END RSA PRIVATE KEY-----";
//	
//	public final static String RSA_PUBLICA = "-----BEGIN PUBLIC KEY-----\r\n" + 
//			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqIiaODnBV2Fex1aohG55\r\n" + 
//			"ua6izlxdY/KXUkYabPTp10AvsT66gshG93kmmmC9qBR2d73QrjaQxyVl0Cell4d1\r\n" + 
//			"gnoOJMzhWq2vpXYj2vJJPrFocurZTMh5F2PcRZLB5xK0whQEaod/jdDH4NryCf3K\r\n" + 
//			"qSk7nmHE2wcj3nXpMBLPCX5kyzvJX+Mg1ErX//8/HuhhcQFJ/rI2/OTOPMtqg0x4\r\n" + 
//			"HQaNP7mBFaS3P2l5vFmu+us07prd2QlfIKhyiLG+IJwrO2c1km2FyWFs+bLmLZe2\r\n" + 
//			"BbBQDIUviN2LNOkkvwN5YtGkEbkmXjNw3Vgx+aAdOU0YaSa6N47DEYHKGLsCwRXZ\r\n" + 
//			"wwIDAQAB\r\n" + 
//			"-----END PUBLIC KEY-----";
//
//}

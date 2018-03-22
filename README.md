# camperaApp
APPLICATION SIDE:
	
	WORK DONE TILL NOW:
		
		1. CLICKING OF PICTURE
		2. SAVING OF PICTURE IN PHONE
		3. FETCHING GEOCOORD AND TIME, AND ENCRYPTING IT TO FORM THE TIMESTAMP.
		4. HASHING THE IMAGE USING MD5, ENCODING THE HASH INTO BASE64 FORMAT.
	
	WORK TO BE DONE:
		
		1. CONVERT THE IMAGE BITMAP INTO BASE64 FORM AND ENCODE IT INTO BASE64. THIS WILL MAKE IT READY FOR TRANSPORTATION.
		2. STORING THE IMAGE, IMAGE HASH, ENCODED TIMESTAMP INTO THE SAME LOCAL DATABASE.
		3. SENDING ALL THIS DATA TO SERVER
		4. MAKE FORM FOR DETAIL SUBMISSION( NAME OF AGENT, DIMENSION OF HOARDING, CITY, NUMBER OF BOARDS ETC)



SERVER SIDE:

	WORK DONE TILL NOW:
		1. NOT DONE ANYTHING AS OF NOW.
		2. HAVE PREPARED THE CODE FOR:
			1. decoding the image sent in base64 for and hash it again in md5 form. this will help us check whether the obtained image is same. ths will be done by checking it against the hash provided by server.
			2. looking for rsa decryption code in python.
	
	WORK TO BE DONE:
		1. do we need to recreate the image in jpeg form to store it on server or the image can be stored as base64 string?

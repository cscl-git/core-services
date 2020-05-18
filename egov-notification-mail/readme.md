## Send emails using Kafka.

Picks up messages from kafka topic `egov.core.notification.email` to send email. 

### Plain text email
```json
{"email":"somebody@example.org.egov","subject":"Testing TL using egov-notification-email","body":"Your trade license has been generated."}]}
```

### HTML Email

Set "html" as true.

```json
{"email":"somebody@example.org.egov","subject":"Testing TL with attachment from Email Service","body":"<div><h4>Your trade license is generated</h4><p>Please keep a copy on you at all times while driving your vehicle.</p></div>", "isHTML": true,
```

Optionally add attachments

```json
{
	"email":"somebody@example.org.egov",
	"subject":"Testing TL with attachment from Email Service",
	"body":"<div><h4>Your trade license is generated</h4><p>Please find your license certificate attached.</p></div>",
	"isHTML": true, 
	"attachments" : [{
		"name" : "Trade license.pdf",
		"url": "http://www.xmlpdf.com/manualfiles/hello-world.pdf", 
		"mimeType" : "application/pdf"
	}]
}
```
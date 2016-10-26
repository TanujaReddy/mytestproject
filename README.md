# mytestproject

Description
GET method with:
description
response for HTTP status 200 (which body's content type is "application/json")
POST method with:
description
"access_token" queryParameter
bodyParameter with "application/json" contentType and validated by a Schema
response with HTTP status 200 (which body's content type is "application/json")

Drag an HTTP component to the canvas.

Studio creates a flow named apiFlow by default.

In the apiflow, select the HTTP connector to open its properties editor.

Click Edit-16x16 to edit the Connector Configuration global configuration element.

Change the value of the Base Path as follows:

customer-list

Click OK.

In the properties editor, change the Path setting from / to /customer-list.

Drag a Set Payload component to the process area of apiFlow.

Set the Value of the payload to customer-list.

Save the changes.

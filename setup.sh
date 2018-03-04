#!/bin/bash

# Delete all users:

curl $1/users -X DELETE

# Create users:

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"joris","password":"tinbergen","mate":"remco","isSuper":true}' # SUPER!

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"remco","password":"vanwijk","mate":"joris","isSuper":false}'

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"valeria","password":"boshnakova","mate":"rene","isSuper":false}'

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"rene","password":"kleizen","mate":"valeria","isSuper":false}'

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"lukas","password":"spee","mate":"vincent","isSuper":false}'

curl $1/users -X POST \
	-H "Content-Type: application/json" \
	-d '{"username":"vincent","password":"dehaan","mate":"lukas","isSuper":false}'
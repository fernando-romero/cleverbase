# Secret-based 2FA API

## Requirements
* sbt.
* local instance of MongoDB running on default port.

## Setup
* start MongoDB.
* run the api with `sbt run`, app will be available at *http://localhost:8080*.
* run `./setup.sh localhost:8080`.

## 2FA
In order to login the api requires you to provide:
* Your credentials, as basic http authentication.
* A secret, which has to be issued by your *mate* (another user), the mate has been set by the setup script. The secret is a text that must be sent in the *Secret* header.

Superusers do not require a secret to login. The setup script defines only one superuser.

## Endpoints

### Get /shared - credentials required
Here you can see all the secrets that have been shared with you, pick one that has your mate as owner and use it to login. Example response:
```
{
    "secrets": [
        {
            "id": "secret1",
            "text": "this is secret 1",
            "owner": "fernando"
        }
    ]
}
```

### Post /login - credentials and secret required
No body needed, just the credentials and the secret header. If successfull you will be logged in and will receive a 204.

### Post /secrets - credentials and login required
Once you have logged in you can create secrets. Example request:
```
{
	"id": "secret1",
	"text": "this is secret 1"
}
```

### Put /secrets - credentials and login required
Once you have logged in you can share secrets so other users that have you as mate can also log in. Example request:
```
{
	"id": "secret1",
	"sharedWith": ["joris"]
}
```
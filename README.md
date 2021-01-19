# spring-minio
You can test this application by starting an instance of MinIO on docker with the command:
```
docker run -p 9000:9000 \
  -e "MINIO_ROOT_USER=AKIAIOSFODNN7EXAMPLE" \
  -e "MINIO_ROOT_PASSWORD=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY" \
  minio/minio server /data
```
to Send a file, use Postman.
Request method: POST
URL: localhost/8080

Body -> form-data -> Key: "file" -> click on the dropdown on the right and select "File", and then import the file in Postman.

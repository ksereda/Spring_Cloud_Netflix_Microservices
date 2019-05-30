# Getting Started

### RU

Запускаем Eureka Server, запускаем наш клиент ftp_bucket_service. Он будет зарегистрирован в Eureka Server.

Идем на URL: 

    http://my-eureka-server-zone1.com:8762/eureka/
    
и увидим там eureka-zone1_server.

Запускаем реплики.
После запуска  Eureka Client, мы можем убедиться, что он находит другиу Eureka Client.

Теперь можно перейти по 

    http://localhost:8090
    
    и
    
    http://localhost:8091

Мы сможем убедиться в том, что наш сервис видит первую реплику "ftp_bucket_service-replica_01" и перейти по `/showAllServiceIds`, чтобы получить список сервисов,
перейти по `/showService` чтобы получить информацию о instance, host и port, также перейти по `/hello` чтобы получить приветствие 

    Hello from 'FTP Bucket Service' :)
    
    
_______________

### ENG

We start Eureka Server, we start our client ftp_bucket_service. He will be registered with Eureka Server.

Go to the URL:

    http://my-eureka-server-zone1.com:8762/eureka/
    
and see eureka-zone1_server there.

Run replicas.
After launching Eureka Client, we can make sure that it finds another Eureka Client.

Now you can go to

    http: // localhost: 8090
    
    and

    http: // localhost: 8091

We will be able to make sure that our service sees the first replica of "ftp_bucket_service-replica_01" and go to `/ showAllServiceIds` to get a list of services,
go to `/ showService` to get information about the instance, host and port, also go to` / hello` to get a greeting

    Hello from 'FTP Bucket Service' :)
# CtrlFlame - Sistema de Monitoramento e Prevenção de Queimadas

##  Sobre o Projeto

O CtrlFlame é um sistema integrado de monitoramento e prevenção de queimadas que combina dados do INPE com uma rede de sensores IoT para fornecer alertas em tempo real e análises históricas de focos de incêndio.

###  Principais Funcionalidades

1. **Monitoramento em Tempo Real**
   - Visualização de dados de sensores em tempo real
   - Medição de temperatura e umidade via sensores DHT11
   - Cálculo automático do nível de risco de incêndio

2. **Mapa de Queimadas**
   - Visualização geográfica de focos de incêndio
   - Dados históricos fornecidos pelo INPE
   - Análise de risco por região

3. **Sistema de Notificações**
   - Alertas via WhatsApp para usuários cadastrados
   - Notificações personalizadas por nível de risco

4. **Área do Usuário**
   - Cadastro e gerenciamento de perfil
   - Registro de número de telefone para alertas

## Como Executar o Projeto

# IMPORTANTE !!!!!!!

## Configuração do Ambiente

1. Configure as variáveis de ambiente (.env)

   No arquivo chamado variaveis.txt anexado junto com a entrega estão os valores que deverão ser subsitituidos no arquivo .env (ou adicionados manualmente)

   ### twilio.account.sid=account_sid_fornecido 
   ### twilio.auth.token=auth_token_fornecido
   ### twilio.whatsapp.from=numero_whatsapp_fornecido

2. Instale as dependências 


3. Execute o projeto
   - ./mvnw spring-boot:run

   - Abra o navegador e acesse: `http://localhost:8080`



## Estrutura do Projeto

### config/      Configurações
### controller/  Controladores
### model/      Entidades
### repository/ Repositórios
### services/   Serviços
### resources/ Templates, dados csv e estilização


## Documentação da API

### URL
```
http://localhost:8080/api
```

### Endpoints

- **Endpoint:** `/sensor-data`
- **Método:** GET
- **Descrição:** Retorna todos os dados dos sensores cadastrados no sistema
- **Resposta esperada:** Lista de objetos SensorData contendo:
  ```json
  [
    {
      "id": "long",
      "deviceId": "string",
      "temperature": "double",
      "humidity": "double",
      "latitude": "double",
      "longitude": "double",
      "timestamp": "datetime",
      "fireRiskLevel": "integer"
    }
  ]
  ```

- **Endpoint:** `/sensor-data/device/{deviceId}`
- **Método:** GET
- **Descrição:** Retorna todos os dados de um sensor específico
- **Parâmetros:**
  - `deviceId` (path): ID do dispositivo
- **Resposta esperada:** Lista de objetos SensorData do dispositivo especificado

- **Endpoint:** `/sensor-data/risk/{level}`
- **Método:** GET
- **Descrição:** Retorna todos os dados de sensores com um determinado nível de risco
- **Parâmetros:**
  - `level` (path): Nível de risco (1 = Baixo, 2 = Médio, 3 = Alto)
- **Resposta esperada:** Lista de objetos SensorData com o nível de risco especificado

### Cache

Os endpoints utilizam cache para melhorar a performance:
- `sensorData`: Cache para todos os dados dos sensores
- `sensorDataByDevice`: Cache para dados por dispositivo
- `sensorDataByRiskLevel`: Cache para dados por nível de risco


## Integrantes

### Carolina Machado 552925
### Iago Diniz 553776
### Rafael Nascimento 553117



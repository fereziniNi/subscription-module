# Subscription Module

Projeto acadêmico desenvolvido com **Java** e **Spring Boot** para gerenciamento de assinaturas, com foco em qualidade de software, modelagem de domínio e testes.

O sistema implementa regras de negócio relacionadas à criação de assinaturas, alteração de plano, renovação de ciclos, cancelamento, geração de faturas e consulta de histórico. O desenvolvimento foi guiado por **User Stories** e cenários em estilo **BDD**, com implementação baseada em **TDD** e complementação por **testes funcionais**.

## Funcionalidades

- Criação de assinaturas com escolha de plano e modalidade
- Alteração de plano com tratamento de upgrade e downgrade
- Renovação de assinaturas ao fim do ciclo
- Cancelamento imediato ou agendado
- Geração de faturas por ciclo
- Consulta de assinaturas com filtros e ordenação
- Exposição das operações por meio de API REST

## Tecnologias utilizadas

- Java
- Spring Boot
- Maven
- JUnit 5
- Mockito
- AssertJ
- JPA

## Observações

- O projeto utiliza uma base de autenticação fornecida pela disciplina.
- As regras de negócio foram mantidas fora das classes de persistência, preservando a separação entre domínio e infraestrutura.
- Os testes foram organizados com foco em serviços da aplicação, utilizando tags para distinguir testes TDD e funcionais.

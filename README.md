# Burger Place App

# :pushpin: Conteúdo

* [Sobre](#Sobre)
* [Tecnologias](#Tecnologias)
* [Screenshots](#Screenshots)
    * [App](#App)
    * [Modelagem](#Modelagem)

<a name="Sobre"></a>
# :hamburger: Sobre

O **Burger Place App** é um sistema web que foi construído  com o intuito de integrar todos os fluxos de processo de uma hamburgueria, que envolve desde a abertura da comanda pela parte do cliente até o preparo pelo cozinheiro e a entrega pelo garçom.

<a name="Tecnologias"></a>
# :zap: Tecnologias

* [Java](https://www.java.com/pt-BR/)
* [Spring Boot](https://spring.io/projects/spring-boot/)
* [Typescript](https://www.typescriptlang.org/)
* [Angular](https://angular.io/)
* [PostgreSQL](https://www.postgresql.org/)

<a name="Screenshots"></a>
# :camera: Screenshots

<a name="App"></a>
## :desktop_computer: App
<p align="center">
 <img src="https://github.com/Franciscocaardoso/delivery-api/blob/main/.github/root_page.png"/>
 <img src="https://github.com/Franciscocaardoso/delivery-api/blob/main/.github/available_boards_page.png"/>
 <img src="https://github.com/Franciscocaardoso/delivery-api/blob/main/.github/customer_page.png"/>
 <img src="https://github.com/Franciscocaardoso/delivery-api/blob/main/.github/customer_page_rating.png"/>
 <img src="https://github.com/Franciscocaardoso/delivery-api/blob/main/.github/kitchen_page.png"/>
</p>

<a name="Modelagem"></a>
## :game_die: Modelagem

### Modelo conceitual
<p align="center">
 <img src="https://github.com/Franciscocaardoso/delivery-api/blob/main/.github/modelo_conceitual.png"/>
</p>

### Modelo lógico
<p align="center">
  <img src="https://github.com/Franciscocaardoso/delivery-api/blob/main/.github/modelo_logico.png"/>
</p>

## :paperclip: Endpoints
<details>
  <summary>
    <strong>products</strong>
  </summary>
<ul>
  <li><strong>GET</strong> /products</li>
  <li><strong>GET</strong> /products/{id}</li>
  <li><strong>POST</strong> /products</li>
  <li><strong>PUT</strong> /products/{id}</li>
  <li><strong>DELETE</strong> /products/{id}</li>
</ul>
</details>

<details>
  <summary>
    <strong>occupations</strong>
  </summary>
<ul>
  <li><strong>GET</strong> /occupations</li>
  <li><strong>GET</strong> /occupations/{occupationId}</li>
  <li><strong>POST</strong> /occupations</li>
  <li><strong>POST</strong> /occupations/{occupationId}/items</li>
  <li><strong>DELETE</strong> /occupations/{occupationId}/items</li>
  <li><strong>DELETE</strong> /occupations/{occupationId}</li>
  <li><strong>PUT</strong> /occupations/{occupationId}/items/{itemId}</li>
  <li><strong>PATCH</strong> /occupations/{occupationId}/items/{itemId}/start-preparation</li>
  <li><strong>PATCH</strong> /occupations/{occupationId}/items/{itemId}/finish-preparation</li>
  <li><strong>PATCH</strong> /occupations/{occupationId}/items/{itemId}/deliver</li>
  <li><strong>PATCH</strong> /occupations/{occupationId}/items/finish</li>
</ul>
</details>

<details>
  <summary>
    <strong>customers</strong>
  </summary>
<ul>
  <li><strong>GET</strong> /customers</li>
  <li><strong>GET</strong> /customers/{id}</li>
  <li><strong>POST</strong> /customers</li>
  <li><strong>PUT</strong> /customers/{id}</li>
  <li><strong>DELETE</strong> /customers/{id}</li>
</ul>
</details>

<details>
  <summary>
    <strong>boards</strong>
  </summary>
<ul>
  <li><strong>GET</strong> /boards</li>
  <li><strong>GET</strong> /boards/{id}</li>
  <li><strong>POST</strong> /boards</li>
  <li><strong>PUT</strong> /boards/{id}</li>
  <li><strong>DELETE</strong> /boards/{id}</li>
</ul>
</details>

<details>
  <summary>
    <strong>review</strong>
  </summary>
<ul>
  <li><strong>GET</strong> /reviews</li>
  <li><strong>GET</strong> /reviews/{id}</li>
  <li><strong>POST</strong> /reviews</li>
  <li><strong>PUT</strong> /reviews/{id}</li>
  <li><strong>DELETE</strong> /reviews/{id}</li>
</ul>
</details>

<details>
  <summary>
    <strong>order-items</strong>
  </summary>
<ul>
  <li><strong>GET</strong> /order-items</li>
</ul>
</details>

/*Adicionando uma entidade com o @Entity e adicionando a criação da tabela no banco de dados
@Table(name = "series") aprendi a definir a estrategia pra geração do meu ID utilizando um
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) utilizei tambem outra anotaçao do hibernate o @Enumerated

 AULA- SALVANDO NO BANCO DE DADOS
 Começei a aula criando uma lista para pegar series e filmes  private
  @Transient
  List<Episodio> episodios = new ArrayList<>();

 Aprendi outra anotaçao do hibernate o @Transient que faz com que nao persista os dados e nem salve depois criei
 um novo pacote o * Repository * botando o nome da entidade com o sufixo repository usando uma interface

   public interface SerieRepository extends JpaRepository<Serie, Long> {
}

Aprendi sobre derived queries
 */
package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.Repository.SerieRepository;
import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSeries = new ArrayList<>();


    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository repositorio) {
    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar séries por Titulo               
                    5 - Buscar séries por Ator
                    6 - Buscar Top 5 Series 
                    7 - Buscar Series por Categoria
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:

                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        //dadosSeries.add(dados);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha serie pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie =  repositorio.findByTituloContaningIgnorecase(nomeSerie);

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo()
                        .replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Serie não encontrada");
        }

    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha serie pelo nome: ");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContaningIgnorecase(nomeSerie);

        if (serieBuscada.isPresent()){
            System.out.println("Dados da Serie " + serieBuscada.get());

        }else {
            System.out.println("Serie não Encontrada");
        }
    }

    private void buscarSeriesPorAtor(){
        System.out.println("Qual nome para Busca");
        var nomeAtor = leitura.nextLine();

        System.out.println("Avaliações apartir de quanto: ");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesEncontradaas =
                repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        System.out.println("Nome das series que " + nomeAtor + "trabalhou: ");
        seriesEncontradaas.forEach(s ->
                System.out.println(s.getTitulo() +"avaliação: "+ s.getAvaliacao())
        );
    }

    private void buscarTop5Series() {
     List<Serie> seriesTop = repositorio.findTop5byOrderByAvaliacaoDesc();
     seriesTop.forEach(s ->
             System.out.println(s.getTitulo() + " Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Voce quer buscar qual genero:");
        var nomeDoGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeDoGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Series da Categoria " + nomeDoGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorAvaliacao(){
        System.out.println("Filtrar series até quantas Temporadas: ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Qual a avaliação ´para busca");
        var avaliacao = leitura.nextDouble();
        List<Serie>  filtroSeries =
                repositorio.findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(totalTemporadas, avaliacao);
        System.out.println("****SERIES FILTRADAS****");
        filtroSeries.forEach(s -> System.out.println(s.getTitulo() + "   - avaliação: " + s.getAvaliacao()) );
    }
}
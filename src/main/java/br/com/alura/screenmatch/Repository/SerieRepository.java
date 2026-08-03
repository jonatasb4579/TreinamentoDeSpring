package br.com.alura.screenmatch.Repository;

import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
        Optional<Serie> findByTituloContaningIgnorecase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor,double avaliacao);

    List<Serie> findTop5byOrderByAvaliacaoDesc();
}

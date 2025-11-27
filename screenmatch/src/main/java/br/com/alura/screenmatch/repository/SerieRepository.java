package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    //Busca série através do título da série
    Optional<Serie> findByTituloContainingIgnoreCase(String serie);

    //Busca séries em que um determinado ator atuou
    Optional<List<Serie>> findByAtoresContainingIgnoreCase(String ator);

    //Busca séries de um ator com avaliações maiores ou iguais ao parâmetro
    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String ator, Double avaliacao);

    //Busca as 5 séries melhores avaliadas utilizando o DESC para pegar da maior a menor
    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    //Busca Séries através de uma categoria
    List<Serie> findByGenero(Categoria categoria);

    //Busca Séries com um número máximo de temporadas e uma avaliação mínima
    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer numeroTemporadas, Double avaliacao);


}

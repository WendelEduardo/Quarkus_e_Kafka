package org.br.mineradora.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.br.mineradora.entity.QuotationEntity;

@ApplicationScoped
public class QuotationRepositoy implements PanacheRepository<QuotationEntity> {



}

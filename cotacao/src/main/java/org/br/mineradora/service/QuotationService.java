package org.br.mineradora.service;

import jakarta.ejb.Local;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Column;
import org.br.mineradora.client.CurrencyPriceClient;
import org.br.mineradora.dto.CurrencyPriceDTO;
import org.br.mineradora.dto.QuotationDTO;
import org.br.mineradora.entity.QuotationEntity;
import org.br.mineradora.message.KafkaEvents;
import org.br.mineradora.repository.QuotationRepositoy;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class QuotationService {

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepositoy quotationRepositoy;

    @Inject
    KafkaEvents kafkaEvents;

    public void getCurrencyPrice(){
        CurrencyPriceDTO currencyPriceDTO = currencyPriceClient.getPriceByPair("USD-BRL");

        if(updateCurrentInfoPrice(currencyPriceDTO)){
            var quotatioDTO = new QuotationDTO(new Date(),
                                               new BigDecimal(currencyPriceDTO.getUsdbrl().getBid()));

            kafkaEvents.sendNewKafkaEvent(quotatioDTO);
        }
    }

    private boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyPriceDTO) {

        BigDecimal currentPrice = new BigDecimal(currencyPriceDTO.getUsdbrl().getBid());
        AtomicBoolean updatePrice = new AtomicBoolean(false);


        List<QuotationEntity> quotationEntityList = quotationRepositoy.findAll().list();

        if(quotationEntityList.isEmpty()){
            saveQuotation(currencyPriceDTO);
            updatePrice.set(true);
        }else{
            QuotationEntity lastDollarPrice =
                    quotationEntityList.get(quotationEntityList.size() - 1);

            if(currentPrice.floatValue() > lastDollarPrice.getCurrencyPrice().floatValue()){
                saveQuotation(currencyPriceDTO);
                updatePrice.set(true);
            }
        }

        return updatePrice.get();
    }

    private void saveQuotation(CurrencyPriceDTO currencyPriceDTO) {
        quotationRepositoy.persist(new QuotationEntity(
                new Date(),
                new BigDecimal(currencyPriceDTO.getUsdbrl().getBid()),
                currencyPriceDTO.getUsdbrl().getPctChange(),
                "USD-BRL"
        ));
    }

}

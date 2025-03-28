package com.lowcost.mapper;

import com.lowcost.dto.PriceAdjustmentDTO;
import com.lowcost.model.PriceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PriceAdjustmentMapper {
    PriceAdjustmentMapper INSTANCE = Mappers.getMapper(PriceAdjustmentMapper.class);

    PriceAdjustmentDTO toPriceAdjustmentDTO(PriceHistory priceHistory);

    PriceHistory toPriceHistory(PriceAdjustmentDTO priceAdjustmentDTO);
}
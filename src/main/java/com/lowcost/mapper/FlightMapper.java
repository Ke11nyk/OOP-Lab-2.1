package com.lowcost.mapper;

import com.lowcost.dto.FlightDTO;
import com.lowcost.model.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FlightMapper {
    FlightMapper INSTANCE = Mappers.getMapper(FlightMapper.class);

    @Mapping(target = "currentPrice", source = "currentPrice")
    FlightDTO toFlightDTO(Flight flight);
}
package td.h.mapper;

import org.apache.ibatis.annotations.Mapper;
import td.h.o.T_Configuration;

@Mapper
public interface ConfigurationMapper {

    T_Configuration getConfig();
    boolean delete();
    boolean save(T_Configuration configuration);
}

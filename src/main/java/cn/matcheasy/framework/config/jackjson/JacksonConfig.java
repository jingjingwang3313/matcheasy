package cn.matcheasy.framework.config.jackjson;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * 在 spring 中使用注解,常使用 @Autowired ,默认是根据类型 Type 来自动注入的.
 * 但有些特殊情况,同一个接口,可能会有几种不同的实现类,处理办法:
 * 1. 用 @Qualifier 注解来解决接口多实现注入的问题, @Qualifier("serviceBeanName") 存在多个实例配合使用.
 * 2. 用 @Primary 告诉 spring 在犹豫的时候默认优先选择哪一个,自动装配时当出现多个 Bean 候选者时,被注解为 @Primary 的 Bean 将作为首选者,否则将抛出异常.
 *
 * @ConditionalOnMissingBean 用来修饰 bean
 * 注册相同类型的 bean 只有一个, 当你注册多个相同的 bean 时, 会出现异常.
 * @class JacksonConfig
 * @author: wangjing
 * @date: 2020/11/6/0006
 * @desc: TODO
 */
//@JsonComponent
@Slf4j
@Configuration
public class JacksonConfig
{
    @Autowired
    private Environment environment;

    @Value("${spring.jackson.date-format}")
    private String pattern;

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder)
    {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // Include.ALWAYS 默认,全部序列化
        // Include.NON_DEFAULT  NULL值不序列化
        // Include.NON_NULL NULL值不序列化
        // Include.NON_EMPTY 空("")或者NULL值都不序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 属性未知映射是否抛异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许出现特殊字符和转义符
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 允许出现单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // NULL键序列化时,将NULL值转为""
        objectMapper.getSerializerProvider().setNullKeySerializer(new NullKeySerializer());
        // NULL值序列化时,将NULL值转为""
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>()
        {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider)
                    throws IOException
            {
                jsonGenerator.writeString("");
            }
        });

        // Float/Double保留两位小数; Long/BigDecimal转换成String,防止返回前端丢失精度;
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(float.class, DoubleChangeSerializer.instance);
        simpleModule.addSerializer(Float.TYPE, DoubleChangeSerializer.instance);
        simpleModule.addSerializer(Float.class, DoubleChangeSerializer.instance);
        simpleModule.addSerializer(double.class, DoubleChangeSerializer.instance);
        simpleModule.addSerializer(Double.TYPE, DoubleChangeSerializer.instance);
        simpleModule.addSerializer(Double.class, DoubleChangeSerializer.instance);
        simpleModule.addSerializer(long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        simpleModule.addSerializer(BigDecimal.class, BigDecimalToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    /**
     * BigDecimal,Double,Float转换为保留两位小数
     */
    private static class DoubleChangeSerializer extends NumberSerializer
    {
        private final static DoubleChangeSerializer instance = new DoubleChangeSerializer();

        private static final long serialVersionUID = 1L;

        /**
         * @param rawType
         * @since 2.5
         */
        public DoubleChangeSerializer(Class<? extends Number> rawType)
        {
            super(rawType);
        }

        public DoubleChangeSerializer()
        {
            super(Number.class);
        }

        @Override
        public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException
        {
            if (value instanceof BigDecimal)
            {
                g.writeObject(Convert.toStr(NumberUtil.round((BigDecimal) value, 2)));
            }
            else if (value instanceof Double)
            {
                g.writeObject(Convert.toStr(NumberUtil.round(value.doubleValue(), 2)));
            }
            else if (value instanceof Float)
            {
                g.writeObject(Convert.toStr(NumberUtil.round(value.floatValue(), 2)));
            }
        }
    }

    /**
     * BigDecimal对象值转为String
     */
    private static class BigDecimalToStringSerializer extends ToStringSerializer
    {

        private final static BigDecimalToStringSerializer instance = new BigDecimalToStringSerializer();

        private static final long serialVersionUID = 1L;

        public BigDecimalToStringSerializer()
        {
            super(Object.class);
        }

        public BigDecimalToStringSerializer(Class<?> handledType)
        {
            super(handledType);
        }

        @Override
        public boolean isEmpty(SerializerProvider prov, Object value)
        {
            if (value == null)
            {
                return true;
            }
            String str = ((BigDecimal) value).stripTrailingZeros().toPlainString();
            return str.isEmpty();
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException
        {
            gen.writeString(((BigDecimal) value).stripTrailingZeros().toPlainString());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException
        {
            return createSchemaNode("string", true);
        }

        @Override
        public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException
        {
            serialize(value, gen, provider);
        }
    }

    /**
     * NULL键序列化时,将NULL转为""
     */
    private static class NullKeySerializer extends StdSerializer<Object>
    {
        private static final long serialVersionUID = 1L;

        public NullKeySerializer()
        {
            this(null);
        }

        public NullKeySerializer(Class<Object> t)
        {
            super(t);
        }

        @Override
        public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused)
                throws IOException, JsonProcessingException
        {
            jsonGenerator.writeFieldName("");
        }
    }

    /**
     * Date类型全局时间格式化
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilder()
    {
        return builder ->
        {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat(pattern);
            df.setTimeZone(tz);
            builder.failOnEmptyBeans(false)
                    .failOnUnknownProperties(false)
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .dateFormat(df);
        };
    }

    /**
     * LocalDate类型全局时间格式化
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer()
    {
        return builder -> builder.serializerByType(LocalDateTime.class, localDateTimeDeserializer());
    }

    @Bean
    public LocalDateTimeSerializer localDateTimeDeserializer()
    {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
    }

}

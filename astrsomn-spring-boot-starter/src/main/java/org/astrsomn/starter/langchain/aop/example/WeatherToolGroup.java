package org.astrsomn.starter.langchain.aop.example;

import dev.langchain4j.agent.tool.Tool;
import org.astrsomn.starter.langchain.aop.annotation.AstroToolGroup;

@AstroToolGroup(
    value = "weather",
    description = "天气查询工具组",
    type = "method"
)
public class WeatherToolGroup {

    @Tool("查询指定城市的当前天气")
    public String getCurrentWeather(String city) {
        return "当前 " + city + " 的天气是晴天，温度25°C";
    }

    @Tool("查询指定城市未来几天的天气预报")
    public String getWeatherForecast(String city, int days) {
        return city + " 未来" + days + "天的天气预报：晴天为主";
    }

    @Tool("查询指定城市的空气质量指数")
    public String getAirQuality(String city) {
        return city + " 的空气质量指数为50，空气质量良好";
    }
}
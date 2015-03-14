package remi.discovery;

import java.io.ObjectInputStream.GetField;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceInfo {

	private String serviceName;
	private InetAddress inetAddress;
	private Map<String,String> parameters = new HashMap<String,String>();

	public ServiceInfo(String serviceName) {
		this.serviceName =serviceName;
	}

	public ServiceInfo(String serviceName, InetAddress inetAddress) {
		this.serviceName =serviceName;
		this.inetAddress = inetAddress;
	}

	public String getServiceName() {
		return serviceName;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}
	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public static ServiceInfo toObject(String text){
		ServiceInfo service = null;
		Pattern p = Pattern.compile("\\{service:'(.*?)'[,][ *](.*?)[']\\}");
		Matcher m = p.matcher(text);
		if( m.matches()){
			service = new ServiceInfo(m.group(1));
			String paramsSt = m.group(2);
			List<String> params = Arrays.asList(paramsSt.split("',[ *]"));
			for(String param : params){
				List<String> keyValue = Arrays.asList(param.split(":[ *]'"));
				service.getParameters().put(keyValue.get(0), keyValue.get(1));
			}
		}
		return service;
	}

	public static String toString(ServiceInfo service){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{service:'");
		buffer.append(service.getServiceName());
		buffer.append("'");
		for(Entry<String, String> param : service.getParameters().entrySet()){
			buffer.append(", ");
			buffer.append(param.getKey());
			buffer.append(": '");
			buffer.append(param.getValue());
			buffer.append("'");
		}
		buffer.append("}");
		return buffer.toString();
	}

}

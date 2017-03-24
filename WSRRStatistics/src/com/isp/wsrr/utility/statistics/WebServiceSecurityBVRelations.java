package com.isp.wsrr.utility.statistics;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import com.isp.wsrr.utility.WSRRUtility;

public class WebServiceSecurityBVRelations {

	private static final Logger nbplog = LogManager.getLogger(WebServiceSecurityBVRelations.class.getName());

	public static void main(String[] args) throws Exception {

		String logFileName = System.getProperty("LogFileName");

		if (logFileName != null && logFileName.length() != 0)
			updateLogger(logFileName, "WebServiceSecurityBVRelationsAppender",
					"com.isp.wsrr.utility.statistics.WebServiceSecurityBVRelations");

		WSRRUtility wsrrutility = new WSRRUtility();

		String url = args[0];
		String legamibv = args[1];
		String path = args[2];
		String user = args[3];
		String password = args[4];

		String sld = null;
		String type = null;
		String acronimo = null;
		String ssa = null;

		String comodo = null;
		boolean verbose = false;

		JSONArray jsae = null;
		JSONArray jsaedett = null;
		JSONObject jso = null;

		nbplog.info("----------------------LegamiBV per la Sicurezza V2.0 Marzo 2017 ---------------------------");
		nbplog.info("");
		nbplog.info("----------------------------------------------------------------------------------------------");

		JSONArray jsa = wsrrutility.getAllObjectsSpecifiedByPrimaryType(
				"http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceProfileExtensions%23ServiceLevelAgreement",
				url, user, password);

		String bsrURISLA = null;
		String res = null;
		String acronimoCons = null;
		String ssaCons = null;
		String typeCons = null;
		comodo = null;
		String primaryType = null;
		StringBuffer record = new StringBuffer();
		HashMap acronimoSSAMap = new HashMap<String, String>();
		String[] endpoints = null;

		PrintWriter legami_servizi_consumer_provider = new PrintWriter(path + legamibv, "UTF-8");
		int i = jsa.length();

		int j = 0;
		int contatore=1;
		while (i > j) {

			jsae = (JSONArray) jsa.getJSONArray(j);
			try {
				jso = (JSONObject) jsae.getJSONObject(0);
				bsrURISLA = (String) jso.get("value");

				jsaedett = wsrrutility.getConsumersFromSLAGeneral(bsrURISLA, url, user, password);

				if (jsaedett != null) {

					res = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "bsrURI");
					primaryType = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0),
							"primaryType");

					if (primaryType != null && (primaryType.contains("SCHOST") || primaryType.contains("SHOST")
							|| primaryType.contains("SOPEN") || primaryType.contains("SCOPEN"))) {

						nbplog.info("---------------------------------------------------------------------");
						nbplog.info("Found SLA : " + bsrURISLA );
						nbplog.info("---------------------------------------------------------------------");

						sld = wsrrutility.getSLDFromSLA(bsrURISLA, url, user, password);

						if (sld != null) {

							String provider = wsrrutility.getProviderFromSLD(sld, url, user, password);

							if (provider != null) {

								type = wsrrutility.getServiceVersionSubTipologyBybsrURI(provider, url, user, password);

								acronimo = wsrrutility.getOwningOrganizationFromGenericObjectByBsrUri(provider, url,
										user, password);

								if (acronimo != null) {

									ssa = (String) acronimoSSAMap.get(acronimo);

									if (ssa == null) {
										ssa = wsrrutility.getSSAFromAcronimo(acronimo, url, user, password);
										if (ssa != null)
											acronimoSSAMap.put(acronimo, ssa);
									}

									if (ssa != null) {

										acronimoCons = null;
										ssaCons = null;

										acronimoCons = wsrrutility.getOwningOrganizationFromGenericObjectByBsrUri(res,
												url, user, password);

										if (acronimoCons != null) {
											ssaCons = (String) acronimoSSAMap.get(acronimoCons);

											if (ssaCons == null) {
												ssaCons = wsrrutility.getSSAFromAcronimo(acronimoCons, url, user,
														password);
												if (ssaCons != null)
													acronimoSSAMap.put(acronimoCons, ssaCons);
											}
											
											if (ssaCons !=null) {

											record.delete(0, record.length());
											typeCons = wsrrutility.getServiceVersionSubTipologyBybsrURI(res, url, user,
													password);

											// consumer
											jsaedett = wsrrutility.getObjectPropertiesData(res,
													"&p1=name&p2=primaryType&p3=description", url, user, password);

											String abilitFunzCons = WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "gep63_ABILITAZ_INFRASTR");

											record.append(WebServiceSecurityBVRelations.padder(30,
													WSRRUtility.getObjectValueFromJSONArrayData(
															(JSONArray) jsaedett.get(0), "name")));

											record.append(WebServiceSecurityBVRelations.padder(50,
													WSRRUtility.getObjectValueFromJSONArrayData(
															(JSONArray) jsaedett.get(0), "description")));

											comodo = WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "primaryType");

											comodo = comodo.substring(comodo.indexOf("#") + 1, comodo.length());

											record.append(WebServiceSecurityBVRelations.padder(10,
													WebServiceSecurityBVRelations.getCatalogName(comodo))); // type

											record.append(WebServiceSecurityBVRelations.padder(20, typeCons));

											record.append(WebServiceSecurityBVRelations.padder(10, acronimoCons));

											record.append(WebServiceSecurityBVRelations.padder(2, ssaCons));

											record.append(WebServiceSecurityBVRelations.padder(10, abilitFunzCons));

											endpoints = wsrrutility
													.getEndpointNameFromBsrUriCatalogAndEnvironmentCheckSecurity(res,
															"Application", true, url, user, password);

											record.append(WebServiceSecurityBVRelations.padder(1023,
													WebServiceSecurityBVRelations.normalizefirst4Endpoint(endpoints)));

											// provider

											jsaedett = wsrrutility.getObjectPropertiesData(provider,
													"&p1=name&p2=primaryType&p3=description", url, user, password);

											String abilitFunzProv = WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "gep63_ABILITAZ_INFRASTR");

											record.append(WebServiceSecurityBVRelations.padder(30,
													WSRRUtility.getObjectValueFromJSONArrayData(
															(JSONArray) jsaedett.get(0), "name")));

											record.append(WebServiceSecurityBVRelations.padder(50,
													WSRRUtility.getObjectValueFromJSONArrayData(
															(JSONArray) jsaedett.get(0), "description")));

											comodo = WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "primaryType");

											comodo = comodo.substring(comodo.indexOf("#") + 1, comodo.length());

											record.append(WebServiceSecurityBVRelations.padder(10,
													WebServiceSecurityBVRelations.getCatalogName(comodo)));

											record.append(WebServiceSecurityBVRelations.padder(20, type));

											record.append(WebServiceSecurityBVRelations.padder(10, acronimo));

											record.append(WebServiceSecurityBVRelations.padder(2, ssa));

											record.append(WebServiceSecurityBVRelations.padder(10, abilitFunzProv));

											jsaedett = wsrrutility.getObjectPropertiesData(sld, "&p1=description", url,
													user, password);

											record.append(WebServiceSecurityBVRelations.padder(20,
													WSRRUtility.getObjectValueFromJSONArrayData(
															(JSONArray) jsaedett.get(0), "description")));

											//wsrrutility.getEndpointNameFromBsrUriSLDEnvironmentCheckSecurity(sld,
											//		"Application", "SOAP", false, url, user, password);
											
											String SOAPEndpointSec[]=null;
											String RESTEndpointSec[]=null;
											String CALLABLEEndpointSec[]=null;
											String GENERALEndpoint[]=null;

											int totalSOAPSec=0;
											int totalRESTSec=0;
											int totalCALLABLESec=0;
											int totalGENERAL=0;
											
											boolean write=true;
											String currentEnpointName=" ";
											
											SOAPEndpointSec=wsrrutility.getEndpointNameFromBsrUriSLDEnvironmentCheckSecurity(sld, "Application", "SOAP", true,url, user, password);
																						
											RESTEndpointSec=wsrrutility.getEndpointNameFromBsrUriSLDEnvironmentCheckSecurity(sld, "Application", "REST", true,url, user, password);;
												
											CALLABLEEndpointSec=wsrrutility.getEndpointNameFromBsrUriSLDEnvironmentCheckSecurity(sld, "Application", "CALLABLE", true,url, user, password);
																		
											GENERALEndpoint=wsrrutility.getEndpointNameFromBsrUriSLDEnvironmentCheckSecurity(sld, "Application", "", false,url, user, password);
										
											totalSOAPSec=WebServiceSecurityBVRelations.valorizedEndpoints(SOAPEndpointSec);
											
											totalRESTSec=WebServiceSecurityBVRelations.valorizedEndpoints(RESTEndpointSec);
											
											totalCALLABLESec=WebServiceSecurityBVRelations.valorizedEndpoints(CALLABLEEndpointSec);
											
											totalGENERAL=WebServiceSecurityBVRelations.valorizedEndpoints(GENERALEndpoint);
											
											if ((totalGENERAL+totalSOAPSec+totalRESTSec+totalCALLABLESec)==0) {
												
												currentEnpointName=" ";
												
											} else{
												
												if ((totalGENERAL + totalSOAPSec +totalRESTSec+ totalCALLABLESec) > 1) write=false;

												if (write) {
																																				
														if (totalGENERAL !=0) currentEnpointName=GENERALEndpoint[0];
														if (totalSOAPSec !=0) currentEnpointName=SOAPEndpointSec[0];
														if (totalRESTSec !=0) currentEnpointName=RESTEndpointSec[0];
														if (totalCALLABLESec !=0) currentEnpointName=CALLABLEEndpointSec[0];																											
													
												}
												
											}
																																									
											if (write) {
												
												record.append(WebServiceSecurityBVRelations.padder(255,currentEnpointName));
												
											} else{											
												
												write=false;
												nbplog.error( "Multi endpoint found for SLA : " + bsrURISLA + " associated to SLD : " + sld);
                                            }
										

											if (write) {
												legami_servizi_consumer_provider.println(record.toString());
												
												System.out.println("Record : "+(contatore) +" len : "+record.toString().length());
												contatore++;
												
											}


											record.delete(0, record.length());
											
											SOAPEndpointSec=null;
											RESTEndpointSec=null;
											CALLABLEEndpointSec=null;											
											GENERALEndpoint=null;
											
										  } else {
												nbplog.error(
														"SSA not found for SLA : " + bsrURISLA + " and Acronimo : " + acronimoCons);
										  }

										} else {
											nbplog.error("Acronimo not found for SLA : " + bsrURISLA
													+ " and Consumer : " + res);
										}

									} else {
										nbplog.error(
												"SSA not found for SLA : " + bsrURISLA + " and Acronimo : " + acronimo);
									}
								} else {
									nbplog.error("Acronimo not found for SLA : " + bsrURISLA + " and Provider : "
											+ provider);
								}

							} else {
								nbplog.error("Provider not found for SLA : " + bsrURISLA);
							}

						} else {

							nbplog.error("SLD not Found for SLA : " + bsrURISLA);
						}

					} else {
						
						nbplog.info("SLA : " +bsrURISLA +" skipped");
					}
				} else {
					nbplog.error("Consumer not Found for SLA : " + bsrURISLA);

				}
			} catch (Exception ex) {
				nbplog.error("Runtime Error Occurred : " + ex.getMessage());
			}

			
			j++;
		}

		legami_servizi_consumer_provider.flush();
		legami_servizi_consumer_provider.close();
		
		nbplog.info("Bash LegamiBV Finished CS");

	}

	private static void log(String message, boolean verbose) {

		if (verbose)
			System.out.println(message);
	}

	private static String padder(int spacenumber, String message) {

		String result = "";

		if (message == null)
			message = " ";
		if (message.length() > spacenumber)
			message = message.substring(0, spacenumber);

		result = String.format("%-" + spacenumber + "s", message);

		return result;

	}

	private static String getCatalogName(String name) {
		String result = "";

		int i = 0;

		i = name.indexOf("ServiceVersion");

		return name.substring(0, i);
	}

	private static String normalizefirst4Endpoint(String[] endpoints) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 4; i++) {

			//sb.append(WebServiceSecurityBVRelations.padder(255, endpoints[i])).append(" ");
			sb.append(endpoints[i]).append(" ");

		}

		return sb.toString();
	}
	
	private static int valorizedEndpoints(String[] endpoints) {

		int count=0;

		for (int i = 0; i < endpoints.length; i++) {

			if (endpoints[i].trim().length() !=0) count ++;

		}

		return count;
	}

	static void updateLogger(String file_name, String appender_name, String package_name) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration configuration = context.getConfiguration();
		Layout<? extends Serializable> old_layout = configuration.getAppender(appender_name).getLayout();

		// delete old appender/logger
		configuration.getAppender(appender_name).stop();
		configuration.removeLogger(package_name);

		// create new appender/logger
		LoggerConfig loggerConfig = new LoggerConfig(package_name, Level.INFO, false);
		FileAppender appender = FileAppender.createAppender(file_name, "false", "false", appender_name, "true", "true",
				"true", "8192", old_layout, null, "false", "", configuration);

		appender.start();
		loggerConfig.addAppender(appender, Level.INFO, null);
		configuration.addLogger(package_name, loggerConfig);

		context.updateLoggers();
	}

}
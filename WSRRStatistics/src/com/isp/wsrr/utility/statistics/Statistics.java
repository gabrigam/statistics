package com.isp.wsrr.utility.statistics;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.isp.wsrr.utility.WSRRUtility;

public class Statistics {

	public static void main(String[] args) throws Exception {

		WSRRUtility wsrrutility = new WSRRUtility();
		Statistics statistics = new Statistics();

		String url = args[0];
		String totalibv = args[1];
		String legamibv = args[2];
		String legamiav = args[3];
		String servizi = args[4];
		String path = args[5];
		String verbose_ = args[6];
		String user = args[7];
		String password = args[8];

		HashMap<String, String> totalMap = new HashMap<String, String>();
		HashMap<Object, String> totalSSAMap = new HashMap<Object, String>();

		HashMap<String, String> dicuiMap = new HashMap<String, String>();
		HashMap<String, String> dicuiSSAMap = new HashMap<String, String>();
		HashMap<String, String> legamiMap = new HashMap<String, String>();
		HashMap<String, String> legamimag2SSAMap = new HashMap<String, String>();
		HashMap<String, String> legamimag2Map = new HashMap<String, String>();
		HashMap<String, String> sldMap = new HashMap<String, String>();
		HashMap acronimoSSAMap = new HashMap<String, String>();

		int consumerNF = 0;
		int providerNF = 0;
		int ssaNF = 0;
		int acronimoNF = 0;
		int sldNF = 0;

		String sld = null;
		String type = null;
		String acronimo = null;
		String ssa = null;
		int totalBA = 0;

		String totalProvider = null;
		String totalProviderSSA = null;
		String comodo = null;
		StringBuffer recordSB = new StringBuffer();
		boolean verbose = false;

		JSONArray jsae = null;
		JSONArray jsaedett = null;
		JSONObject jso = null;
		
		String runtime=null;
		String designtime=null;
		String userdeftime=null;

		if (verbose_ != null) {

			if (verbose_.equalsIgnoreCase("Y"))
				verbose = true;

		}

		System.out.println("----------------------Totalizzatore V3.3- Marzo 2017-----------------------------");
		System.out.println("Utilizzata la nuova funzione per il recupero della sottotipologia: getServiceVersionTipologyBybsrURI");
		System.out.println("Aggiunto null in caso di valore non presente per runtime,designtime e userdeftime");
		System.out.println("Aggiunta gestione della versione");
		System.out.println("----------------------------------------------------------------------------------");
		JSONArray jsaBusinessApplication = wsrrutility.getAllObjectsSpecifiedByPrimaryType(
				"http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel%23BusinessApplication",
				url, user, password);

		if (jsaBusinessApplication != null) {

			totalBA = jsaBusinessApplication.length();
		}
		JSONArray jsaSHOST = wsrrutility.getAllObjectsSpecifiedByPrimaryType(
				"http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel%23SHOSTServiceVersion",
				url, user, password);
		JSONArray jsaSCHOST = wsrrutility.getAllObjectsSpecifiedByPrimaryType(
				"http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel%23SCHOSTServiceVersion",
				url, user, password);
		JSONArray jsaSOPEN = wsrrutility.getAllObjectsSpecifiedByPrimaryType(
				"http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel%23SOPENServiceVersion",
				url, user, password);
		JSONArray jsaSCOPEN = wsrrutility.getAllObjectsSpecifiedByPrimaryType(
				"http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel%23SCOPENServiceVersion",
				url, user, password);
		System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		System.out.println("Calcolo Totale dei servizi in corso......");

		Statistics.updateTotals(wsrrutility, jsaSHOST, totalMap, totalSSAMap, url, user, password);
		Statistics.updateTotals(wsrrutility, jsaSCHOST, totalMap, totalSSAMap, url, user, password);
		Statistics.updateTotals(wsrrutility, jsaSOPEN, totalMap, totalSSAMap, url, user, password);
		Statistics.updateTotals(wsrrutility, jsaSCOPEN, totalMap, totalSSAMap, url, user, password);

		System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		System.out.println("Calcolo Totale dei servizi terminato......");

		PrintWriter elenco_servizi = new PrintWriter(path + servizi, "UTF-8");

		System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		
		System.out.println("Recupero dei Business Services......");
		
		Statistics.serviceList(wsrrutility, "http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel%23SHOSTServiceVersion", elenco_servizi, url, user, password);

		Statistics.serviceList(wsrrutility, "http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel%23SCHOSTServiceVersion", elenco_servizi, url, user, password);
		
		Statistics.serviceList(wsrrutility, "http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel%23SOPENServiceVersion", elenco_servizi, url, user, password);

		Statistics.serviceList(wsrrutility, "http://www.ibm.com/xmlns/prod/serviceregistry/profile/v6r3/GovernanceEnablementModel%23SCOPENServiceVersion", elenco_servizi, url, user, password);
		
		System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		System.out.println("Recupero dei Business Services terminato..");

		totalProvider = totalMap.toString();
		totalProviderSSA = totalSSAMap.toString();

		jsaSHOST = null;
		jsaSCHOST = null;
		jsaSOPEN = null;
		jsaSCOPEN = null;
		totalMap = null;
		totalSSAMap = null;
		System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		System.out.println("Calcolo Totale dei servizi con provider =1 e >2 in corso..");

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

		PrintWriter totale_servizi = new PrintWriter(path + totalibv, "UTF-8");

		PrintWriter legami_servizi_consumer_provider = new PrintWriter(path + legamibv, "UTF-8");

		PrintWriter applicazioni_consumer_provider = new PrintWriter(path + legamiav, "UTF-8");

		int i = jsa.length();
		System.out.println("Trovati - " + i + " oggetti SLA");
		System.out.println("");
		int j = 0;
		while (i > j) {

			jsae = (JSONArray) jsa.getJSONArray(j);
			try {
				jso = (JSONObject) jsae.getJSONObject(0);
				bsrURISLA = (String) jso.get("value");
				System.out.println("---------------------------------------------------------------------");
				System.out.println("Current SLA - " + bsrURISLA);
				System.out.println("---------------------------------------------------------------------");

				jsaedett = wsrrutility.getConsumersFromSLAGeneral(bsrURISLA, url, user, password);

				if (jsaedett != null) {

					res = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "bsrURI");
					primaryType = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0),
							"primaryType");

					log("Consumer - " + res, verbose);

					sld = wsrrutility.getSLDFromSLA(bsrURISLA, url, user, password);

					if (sld != null) {

						log("SLD - " + sld, verbose);

						String provider = wsrrutility.getProviderFromSLD(sld, url, user, password);

						if (provider != null) {

							log("Provider - " + provider, verbose);

							//210117 inserito nuovo metodo
							//type = wsrrutility.getServiceVersionClassification(provider, url, user, password);
							type = wsrrutility.getServiceVersionSubTipologyBybsrURI(provider, url, user ,password);

							log("Type - " + type, verbose);

							acronimo = wsrrutility.getOwningOrganizationFromGenericObjectByBsrUri(provider, url, user,
									password);

							if (acronimo != null) {

								ssa = (String) acronimoSSAMap.get(acronimo);

								if (ssa == null) {
									ssa = wsrrutility.getSSAFromAcronimo(acronimo, url, user, password);
									if (ssa != null)
										acronimoSSAMap.put(acronimo, ssa);
								}

								if (ssa != null) {

									log("SSA - " + ssa, verbose);

									if (primaryType != null && (primaryType.contains("SCHOST")
											|| primaryType.contains("SHOST") || primaryType.contains("SOPEN")
											|| primaryType.contains("SCOPEN"))) {

										if (!sldMap.containsKey(sld)) {

											if (dicuiMap.containsKey(type)) {
												int current = Integer.parseInt((String) dicuiMap.get(type));
												current++;
												dicuiMap.put(type, String.valueOf(current));
											} else {
												dicuiMap.put(type, "1");
											}

											if (dicuiSSAMap.containsKey(ssa + " " + type)) {
												int current = Integer
														.parseInt((String) dicuiSSAMap.get(ssa + " " + type));
												current++;
												dicuiSSAMap.put(ssa + " " + type, String.valueOf(current));
											} else {
												dicuiSSAMap.put(ssa + " " + type, "1");
											}

											sldMap.put(sld, "");

										} else {
											int current = 0;

											if (legamiMap.containsKey(sld)) {
												current = Integer.parseInt((String) legamiMap.get(sld));
												current++;
												legamiMap.put(sld, String.valueOf(current));
											} else {
												current = 2;
												legamiMap.put(sld, "2");
											}

											if (current == 2) {

												if (legamimag2Map.containsKey(type)) {
													current = Integer.parseInt((String) legamimag2Map.get(type));
													current++;
													legamimag2Map.put(type, String.valueOf(current));
												} else {
													legamimag2Map.put(type, "1");
												}

												if (legamimag2SSAMap.containsKey(ssa + " " + type)) {
													current = Integer
															.parseInt((String) legamimag2SSAMap.get(ssa + " " + type));
													current++;
													legamimag2SSAMap.put(ssa + " " + type, String.valueOf(current));
												} else {
													legamimag2SSAMap.put(ssa + " " + type, "1");
												}

											}

										}
									} // ServiceVersion

									acronimoCons = null;
									ssaCons = null;

									acronimoCons = wsrrutility.getOwningOrganizationFromGenericObjectByBsrUri(res, url,
											user, password);

									if (acronimoCons != null) {
										ssaCons = (String) acronimoSSAMap.get(acronimoCons);

										if (ssaCons == null) {
											ssaCons = wsrrutility.getSSAFromAcronimo(acronimoCons, url, user, password);
											if (ssaCons != null)
												acronimoSSAMap.put(acronimoCons, ssaCons);
										}

									}

									if (primaryType != null && (primaryType.contains("SCHOST")
											|| primaryType.contains("SHOST") || primaryType.contains("SOPEN")
											|| primaryType.contains("SCOPEN"))) {

										recordSB.delete(0, recordSB.length());
										//210117 inserito nuovo metodo
										//typeCons = wsrrutility.getServiceVersionClassification(res, url, user,password);
										typeCons = wsrrutility.getServiceVersionSubTipologyBybsrURI(res, url, user ,password);

										// -------------------------Legami
										// servizi
										// consumer provider

										// consumer
										jsaedett = wsrrutility.getObjectPropertiesData(res,
												"&p1=name&p2=primaryType&p3=description", url, user, password);

										recordSB.append(WSRRUtility
												.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "name"))
												.append("@#@");

										recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "description")).append("@#@");

										comodo = WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "primaryType");

										comodo = comodo.substring(comodo.indexOf("#") + 1, comodo.length());

										recordSB.append(comodo).append("@#@");

										recordSB.append(typeCons).append("@#@");

										recordSB.append(acronimoCons).append("@#@");

										recordSB.append(ssaCons).append("@#@");

										// 13.08.2016

									    //13.12.16 inseriti i campi di ritorno: gpx63_RUNTIME - gpx63_DESIGNTIME - gpx63_USERDEFTIME
										
										jsaedett = wsrrutility.getObjectPropertiesData(bsrURISLA,
												"&p1=gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_APPL&p2=gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_SYST&p3=gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_PROD&p4=gpx63_RUNTIME&p5=gpx63_DESIGNTIME&p6=gpx63_USERDEFTIME",
												url, user, password);
										
										designtime=WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "gpx63_DESIGNTIME");
										runtime=WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "gpx63_RUNTIME");										
										userdeftime=WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "gpx63_gpx63_USERDEFTIME");

										recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_APPL"))
												.append("@#@");

										recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_SYST"))
												.append("@#@");

										recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_PROD"))
												.append("@#@");

										// provider

										jsaedett = wsrrutility.getObjectPropertiesData(provider,
												"&p1=name&p2=primaryType&p3=description", url, user, password);

										recordSB.append(WSRRUtility
												.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "name"))
												.append("@#@");

										recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "description")).append("@#@");

										comodo = WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "primaryType");

										comodo = comodo.substring(comodo.indexOf("#") + 1, comodo.length());

										recordSB.append(comodo).append("@#@");
										
										recordSB.append(type).append("@#@");

										jsaedett = wsrrutility.getObjectPropertiesData(sld, "&p1=description", url,
												user, password);

										recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
												(JSONArray) jsaedett.get(0), "description")).append("@#@");

										recordSB.append(acronimo).append("@#@");

										recordSB.append(ssa).append("@#@");

										// 13.08.2016 ultimo_utilizzo aggiunto il 24.08.2016 i singoli ambienti

										jsaedett = wsrrutility.getEndpointInfoFromBsrUriCatalogAndEnvironment(provider, "Application",url, user, password);
										
										if (jsaedett != null && jsaedett.length() !=0) {
											recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "sm63_DATA_ULTIMO_UTILIZZO")).append("@#@");
										} else {
											recordSB.append("@#@");
											
										}

										jsaedett = wsrrutility.getEndpointInfoFromBsrUriCatalogAndEnvironment(provider, "SystemTest",url, user, password);
										
										if (jsaedett != null && jsaedett.length() !=0) {
											recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "sm63_DATA_ULTIMO_UTILIZZO")).append("@#@");
										} else {
											recordSB.append("@#@");
											
										}

										jsaedett = wsrrutility.getEndpointInfoFromBsrUriCatalogAndEnvironment(provider, "Produzione",url, user, password);
										
										if (jsaedett != null && jsaedett.length() !=0) {
											recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "sm63_DATA_ULTIMO_UTILIZZO")).append("@#@"); //13.12.16 aggiunto @
										} else {
											recordSB.append("@#@");
											
										}
										
										//13.12.16  in coda aggiungo gpx63_RUNTIME gpx63_DESIGNTIME  gpx63_USERDEFTIME
										
										//09032017 se non valorizzati passo null
										
										if (runtime==null) runtime="null";
										if (runtime.length()==0) runtime="null";
										
										if (designtime==null) designtime="null";
										if (designtime.length()==0) designtime="null";
										
										if (userdeftime==null) userdeftime="null";
										if (userdeftime.length()==0) userdeftime="null";
										
										recordSB.append(runtime)
												.append("@#@");
										
										recordSB.append(designtime)
												.append("@#@");
										
										recordSB.append(userdeftime);

										legami_servizi_consumer_provider.println(recordSB.toString());

										recordSB.delete(0, recordSB.length());

									}

									else {

										if (primaryType.contains("ApplicationVersion")) {

											recordSB.delete(0, recordSB.length());

											recordSB.append(acronimoCons).append("@#@");

											recordSB.append(ssaCons).append("@#@");
											
											// 13.08.2016

											//13.12.16 inseriti i campi di ritorno: gpx63_RUNTIME - gpx63_DESIGNTIME - gpx63_USERDEFTIME
											
											jsaedett = wsrrutility.getObjectPropertiesData(bsrURISLA,
													"&p1=gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_APPL&p2=gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_SYST&p3=gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_PROD&p4=gpx63_RUNTIME&p5=gpx63_DESIGNTIME&p6=gpx63_USERDEFTIME",
													url, user, password);
											
											designtime=WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "gpx63_DESIGNTIME");
											runtime=WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "gpx63_RUNTIME");										
											userdeftime=WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "gpx63_gpx63_USERDEFTIME");

											recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0),
													"gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_APPL")).append("@#@");

											recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0),
													"gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_SYST")).append("@#@");

											recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0),
													"gpx63_DATA_ULTIMO_UTILIZZO_LEGAME_PROD")).append("@#@");


											jsaedett = wsrrutility.getObjectPropertiesData(provider,
													"&p1=name&p2=primaryType&p3=description", url, user, password);

											recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "name")).append("@#@");

											recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "description")).append("@#@");

											comodo = WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "primaryType");

											comodo = comodo.substring(comodo.indexOf("#") + 1, comodo.length());

											recordSB.append(comodo).append("@#@");
											
											recordSB.append(type).append("@#@");
				
											jsaedett = wsrrutility.getObjectPropertiesData(sld, "&p1=description", url,
													user, password);

											recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
													(JSONArray) jsaedett.get(0), "description")).append("@#@");

											recordSB.append(acronimo).append("@#@");

											recordSB.append(ssa).append("@#@");

											// 13.08.2016 ultimo_utilizzo aggiunto il 24.08.2016 i singoli ambienti

											jsaedett = wsrrutility.getEndpointInfoFromBsrUriCatalogAndEnvironment(provider, "Application",url, user, password);
											
											if (jsaedett != null && jsaedett.length() !=0) {
												recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
														(JSONArray) jsaedett.get(0), "sm63_DATA_ULTIMO_UTILIZZO")).append("@#@");
											} else {
												recordSB.append("@#@");
												
											}

											jsaedett = wsrrutility.getEndpointInfoFromBsrUriCatalogAndEnvironment(provider, "SystemTest",url, user, password);
											
											if (jsaedett != null && jsaedett.length() !=0) {
												recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
														(JSONArray) jsaedett.get(0), "sm63_DATA_ULTIMO_UTILIZZO")).append("@#@");
											} else {
												recordSB.append("@#@");
												
											}

											jsaedett = wsrrutility.getEndpointInfoFromBsrUriCatalogAndEnvironment(provider, "Produzione",url, user, password);
											
											if (jsaedett != null && jsaedett.length() !=0) {
												recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData(
														(JSONArray) jsaedett.get(0), "sm63_DATA_ULTIMO_UTILIZZO")).append("@#@"); //13.12.16 aggiunto @
											} else {
												recordSB.append("@#@");
												
											}
											
											//13.12.16  in coda aggiungo gpx63_RUNTIME gpx63_DESIGNTIME  gpx63_USERDEFTIME
											
											//09032017 se non valorizzati passo null
											
											if (runtime==null) runtime="null";
											if (runtime.length()==0) runtime="null";
											
											if (designtime==null) designtime="null";
											if (designtime.length()==0) designtime="null";
											
											if (userdeftime==null) userdeftime="null";
											if (userdeftime.length()==0) userdeftime="null";
											
											recordSB.append(runtime)
											.append("@#@");
									
											recordSB.append(designtime)
											.append("@#@");
									
											recordSB.append(userdeftime);

											applicazioni_consumer_provider.println(recordSB.toString());

											recordSB.delete(0, recordSB.length());

										}
									}

								} else {
									log("SSA - not Found for Acronimo - " + acronimo, verbose);
									ssaNF++;

								}
							} else {
								log("Acronimo - " + " not Found for provider - " + provider, verbose);
								acronimoNF++;
							}

						} else {
							log("Provider not Found", verbose);
							providerNF++;
						}

					} else {

						log("SLD not Found", verbose);
						sldNF++;
					}

				} else {
					log("Consumer not Found", verbose);
					consumerNF++;
				}
				// }//
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			j++;
		}

		legami_servizi_consumer_provider.flush();
		legami_servizi_consumer_provider.close();
		applicazioni_consumer_provider.flush();
		applicazioni_consumer_provider.close();
		elenco_servizi.flush();
		elenco_servizi.close();

		System.out.println("");
		System.out.println("---------------------Risultato------------------------------------");
		System.out.println("Numero totale - " + totalProvider);
		System.out.println("Numero totale x SSA - " + totalProviderSSA);

		System.out.println("Servizi con almeno un provider - " + dicuiMap.toString());
		System.out.println("Servizi con almeno un provider x SSA - " + dicuiSSAMap.toString());

		System.out.println("Servizi con almeno 2 provider - " + legamimag2Map.toString());
		System.out.println("Servizi con almeno 2 provider x SSA - " + legamimag2SSAMap.toString());

		System.out.println("----------------- Anomalie riscontrate-------------------------- ");
		System.out.println("Consumer NF " + consumerNF);
		System.out.println("Provider NF " + providerNF);
		System.out.println("SLD      NF " + sldNF);
		System.out.println("Acronimo NF " + acronimoNF);
		System.out.println("SSA      NF " + ssaNF);

		System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		System.out.println("Calcolo Totale dei servizi con provider =1 e >2 in terminato ..");

		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

		totale_servizi.println("{" + totalBA + "}");
		totale_servizi.println(totalProvider);
		totale_servizi.println(totalProviderSSA);
		totale_servizi.println(dicuiMap.toString());
		totale_servizi.println(dicuiSSAMap.toString());
		totale_servizi.println(legamimag2Map.toString());
		totale_servizi.println(legamimag2SSAMap.toString());
		totale_servizi.flush();
		totale_servizi.close();

		System.out.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
		System.out.println("Elaborazione conclusa .. CS");
	}

	private static void log(String message, boolean verbose) {

		if (verbose)
			System.out.println(message);
	}

	public static void updateTotals(WSRRUtility wsrrutility, JSONArray data, HashMap<String, String> totalMap,
			HashMap<Object, String> totalSSAMap, String url, String user, String password) {

		int i = data.length();
		int j = 0;
		JSONArray jsae = null;
		JSONObject jso = null;
		String bsrUri = null;
		String type = null;
		String acronimo = null;
		String ssa = null;
		int current = 0;
		HashMap<String, String> acronimoSSAMap = new HashMap<String, String>();
		while (i > j) {

			jsae = (JSONArray) data.getJSONArray(j);
			try {
				jso = (JSONObject) jsae.getJSONObject(0);
				bsrUri = (String) jso.get("value");
				//210117 inserito nuovo metodo
				//type = wsrrutility.getServiceVersionClassification(bsrUri, url, user, password);
				type =wsrrutility.getServiceVersionSubTipologyBybsrURI(bsrUri, url, user ,password);
				
				acronimo = wsrrutility.getOwningOrganizationFromGenericObjectByBsrUri(bsrUri, url, user, password);

				if (acronimo != null) {

					ssa = (String) acronimoSSAMap.get(acronimo);

					if (ssa == null) {
						ssa = wsrrutility.getSSAFromAcronimo(acronimo, url, user, password);
						if (ssa != null)
							acronimoSSAMap.put(acronimo, ssa);
					}

					if (ssa != null) {

						if (totalMap.containsKey(type)) {
							current = Integer.parseInt((String) totalMap.get(type));
							current++;
							totalMap.put(type, String.valueOf(current));
						} else {
							totalMap.put(type, "1");
						}

						if (totalSSAMap.containsKey(ssa + " " + type)) {
							current = Integer.parseInt((String) totalSSAMap.get(ssa + " " + type));
							current++;
							totalSSAMap.put(ssa + " " + type, String.valueOf(current));
						} else {
							totalSSAMap.put(ssa + " " + type, "1");
						}

					} else {
						System.out.println("SSA not Found ,for SV : " + bsrUri);
					}

				} else {

					System.out.println("Acronimo not Found ,for SV : " + bsrUri);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			j++;
		}

	}
	
  static  private   String lastUsedDate(WSRRUtility wsrrutility,String bsrURI,String url,String user,String password) {
		
		
		StringBuffer sb=new StringBuffer();		
		String lastUsedDate=null;
		String typeService=null;
		String composedApplication=null;
		String composedSystemTest=null;
		String composedProduzione=null;
		boolean usedflag=false;
		
		JSONArray jsaedett =null;
		jsaedett = wsrrutility.getEndpointInfoFromBsrUriCatalogAndEnvironment(bsrURI, "Application",url, user, password);
		
		int ii = jsaedett.length();
		int jj = 0;
		
		while (ii > jj) {
			
			lastUsedDate=WSRRUtility.getObjectValueFromJSONArrayData(
					(JSONArray) jsaedett.get(jj), "sm63_DATA_ULTIMO_UTILIZZO");
			
			typeService=WSRRUtility.getObjectValueFromJSONArrayData(
					(JSONArray) jsaedett.get(jj), "sm63_endpointType");
			
			if (typeService != null && typeService.length()!=0 && lastUsedDate !=null && lastUsedDate.length()!=0) {
				
				sb.append("(").append(typeService).append(")").append(lastUsedDate);
				usedflag=true;
			}
			
			jj++;
			
		}
		
		composedApplication="";
		
		if (usedflag){
			
			composedApplication=sb.toString();
		}
		
		sb.delete(0, sb.length());
		
		jsaedett =null;
		
		jsaedett = wsrrutility.getEndpointInfoFromBsrUriCatalogAndEnvironment(bsrURI, "SystemTest",url, user, password);
		
		usedflag=false;
		ii = jsaedett.length();
		jj = 0;
		
		while (ii > jj) {
			
			lastUsedDate=WSRRUtility.getObjectValueFromJSONArrayData(
					(JSONArray) jsaedett.get(jj), "sm63_DATA_ULTIMO_UTILIZZO");
			
			typeService=WSRRUtility.getObjectValueFromJSONArrayData(
					(JSONArray) jsaedett.get(jj), "sm63_endpointType");
			
			if (typeService != null && typeService.length()!=0 && lastUsedDate !=null && lastUsedDate.length()!=0) {
				
				sb.append("(").append(typeService).append(")").append(lastUsedDate);
				usedflag=true;
			}
			
			jj++;
			
		}
		
		composedSystemTest="";
		
		if (usedflag) {
			
			composedSystemTest=sb.toString();
		}
		
		sb.delete(0, sb.length());
		
		jsaedett =null;
		jsaedett = wsrrutility.getEndpointInfoFromBsrUriCatalogAndEnvironment(bsrURI, "Produzione",url, user, password);
		
		ii = jsaedett.length();
		jj = 0;
		
		usedflag=false;
			
		while (ii > jj) {
			
			lastUsedDate=WSRRUtility.getObjectValueFromJSONArrayData(
					(JSONArray) jsaedett.get(jj), "sm63_DATA_ULTIMO_UTILIZZO");
			
			typeService=WSRRUtility.getObjectValueFromJSONArrayData(
					(JSONArray) jsaedett.get(jj), "sm63_endpointType");
			
			if (typeService != null && typeService.length()!=0 && lastUsedDate !=null && lastUsedDate.length()!=0) {
				
				sb.append("(").append(typeService).append(")").append(lastUsedDate);
				usedflag=true;
			}
			
			jj++;
			
		}
		
		composedProduzione="";
		
		if (usedflag) {
			composedProduzione=sb.toString();
		}

		sb.delete(0, sb.length());
		
	    StringBuffer composed=new StringBuffer();
	    
	    composed.append(composedApplication).append("@#@");
	    composed.append(composedSystemTest).append("@#@");
	    composed.append(composedProduzione);
		
		return composed.toString();
		
		
	}
  
  static private void serviceList(WSRRUtility wsrrutility,String type,PrintWriter elenco_servizi,String url,String user,String password) {
	  
		JSONArray jsa = wsrrutility.getAllObjectsSpecifiedByPrimaryType(type,url, user, password);

		StringBuffer recordSB=new StringBuffer();
		HashMap acronimoSSAMap = new HashMap<String, String>();
		int i = jsa.length();

		String name = null;
		String ssaBV=null;
		int j = 0;
		while (i > j) {

			recordSB.delete(0, recordSB.length());

			JSONArray jsae = (JSONArray) jsa.getJSONArray(j);
			JSONObject jso = (JSONObject) jsae.getJSONObject(0);
			String bsrURI = (String) jso.get("value");

			//09032017 aggiungo anche la versione
			JSONArray jsaedett = wsrrutility.getObjectPropertiesData(bsrURI,
					"&p1=name&p2=primaryType&p3=description&p4=gep63_ATTIVATO_IN_APPL&p5=gep63_ATTIVATO_IN_SYST&p6=gep63_ATTIVATO_IN_PROD&p7=version",
					url, user, password);

			String attappl = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0),
					"gep63_ATTIVATO_IN_APPL");
			String attsys = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "gep63_ATTIVATO_IN_SYST");
			String attprd = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "gep63_ATTIVATO_IN_PROD");
			//09032017 estraggo la versione
			String version= WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "version");
			//210117 inserito nuovo metodo
			//String sottotBV = wsrrutility.getServiceVersionClassification(bsrURI, url, user, password);
			String sottotBV = wsrrutility.getServiceVersionSubTipologyBybsrURI(bsrURI, url, user ,password);
            System.out.println("Sotto tipologia BV : " + sottotBV);
			
			String acroBV = wsrrutility.getOwningOrganizationFromGenericObjectByBsrUri(bsrURI, url, user, password);

			if (acroBV != null) {

				ssaBV = (String) acronimoSSAMap.get(acroBV);

				if (ssaBV == null) {
					ssaBV = wsrrutility.getSSAFromAcronimo(acroBV, url, user, password);
					if (ssaBV != null)
						acronimoSSAMap.put(acroBV, ssaBV);
				}
			}

			name = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "name");

			recordSB.append(name).append("@#@");

			String comodo = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "primaryType");

			comodo = comodo.substring(comodo.indexOf("#") + 1, comodo.length());

			recordSB.append(comodo).append("@#@");

			recordSB.append(sottotBV).append("@#@");

			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "description"))
					.append("@#@");

			recordSB.append(acroBV).append("@#@");

			if (acroBV != null)
				jsaedett = wsrrutility.getPropertiesByObjectNameAndPrimaryType(acroBV,
						"http://www.ibm.com/xmlns/prod/serviceregistry/v6r3/ALEModel%23Organization", "&p1=description",
						url, user, password);

			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "description"))
					.append("@#@");
	
			//30.08.2016 nuova richiesta vengono recuperate informazioni estese da acronimo
			JSONArray orgDett=null;
			
			if (acroBV != null) {
			
			orgDett=wsrrutility.getPropertiesByObjectNameAndPrimaryType(acroBV, "http://www.ibm.com/xmlns/prod/serviceregistry/v6r3/ALEModel%23Organization", "&p1=ale63_RESP_UFFICIO_MATRICOLA&p2=ale63_CODICE_SISTEMA_APPLICATIVO&p3=ale63_RESP_FUNZIONALE_NOMINATIVO&p4=ale63_RESP_SERVIZIO_NOMINATIVO&p5=ale63_RESP_ATTIVITA_NOMINATIVO&p6=ale63_RESP_ATTIVITA_MATRICOLA&p7=ale63_RESP_TECNICO_NOMINATIVO&p8=ale63_RESP_FUNZIONALE_MATRICOLA&p9=ale63_RESP_SERVIZIO_MATRICOLA&p10=ale63_RESP_UFFICIO_NOMINATIVO&p11=ale63_RESP_TECNICO_MATRICOLA", url, user, password);
			
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_FUNZIONALE_MATRICOLA")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_FUNZIONALE_NOMINATIVO")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_TECNICO_MATRICOLA")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_TECNICO_NOMINATIVO")).append("@#@");
	 
			} else {
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");

			}
			recordSB.append(ssaBV).append("@#@");

			if (ssaBV != null)
				jsaedett = wsrrutility.getPropertiesByObjectNameAndPrimaryType(ssaBV,
						"http://www.ibm.com/xmlns/prod/serviceregistry/v6r3/ALEModel%23Organization", "&p1=description",
						url, user, password);

			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(0), "description"))
					.append("@#@");
			
			//30.08.2016 nuova richiesta vengono recuperate informazioni estese da SSA
			
			if (ssaBV != null) {
				
			orgDett	=null;
			orgDett=wsrrutility.getPropertiesByObjectNameAndPrimaryType(ssaBV, "http://www.ibm.com/xmlns/prod/serviceregistry/v6r3/ALEModel%23Organization", "&p1=ale63_RESP_UFFICIO_MATRICOLA&p2=ale63_CODICE_SISTEMA_APPLICATIVO&p3=ale63_RESP_FUNZIONALE_NOMINATIVO&p4=ale63_RESP_SERVIZIO_NOMINATIVO&p5=ale63_RESP_ATTIVITA_NOMINATIVO&p6=ale63_RESP_ATTIVITA_MATRICOLA&p7=ale63_RESP_TECNICO_NOMINATIVO&p8=ale63_RESP_FUNZIONALE_MATRICOLA&p9=ale63_RESP_SERVIZIO_MATRICOLA&p10=ale63_RESP_UFFICIO_NOMINATIVO&p11=ale63_RESP_TECNICO_MATRICOLA", url, user, password);
		
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_FUNZIONALE_MATRICOLA")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_FUNZIONALE_NOMINATIVO")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_TECNICO_MATRICOLA")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_TECNICO_NOMINATIVO")).append("@#@");
			
			} else {
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");
			}

			recordSB.append(attappl).append("@#@");
			recordSB.append(attsys).append("@#@");
			recordSB.append(attprd).append("@#@");

			//09032017 inserita la versione invece di '00' fisso
			jsaedett = wsrrutility.getAssociatedInterfaces(name, version, url, user, password);
			//jsaedett = wsrrutility.getAssociatedInterfaces(name, "00", url, user, password);

			if (jsaedett != null && jsaedett.length()!=0) {

				int ii = jsaedett.length();
				int jj = 0;
				String interf = null;
				StringBuffer sb = new StringBuffer();
				while (ii > jj) {

					interf = WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) jsaedett.get(jj), "name");

					interf = interf.substring(interf.lastIndexOf("_") + 1, interf.length());
					sb.append(interf).append("|");

					jj++;
				}

				interf = sb.toString();
				interf = interf.substring(0, interf.length() - 1);
				recordSB.append(interf).append("@#@");
				//09032017 scrivo la versione
				recordSB.append(version).append("@#@");
				
			} else
				
			    {
				//08032017 in attesa di recuperare le versioni come wr al posto dell'interfaccia scrivo: ???
				System.out.println("Interfaccia - "+name +" non presente con versione "+version);
				recordSB.append("???").append("@#@");
			    }
				
			
			//30.08.2016 per ogni censimento dati il bsrURI recupero per ogni ambiente: Application-SystemTest-Produzione la lista della data ultimo
			//utilizzo mettendo prima il tipo di endpoint 
			
			//es (CICS)10-10-2018|(REST)20-12_2016|
			
			recordSB.append(Statistics.lastUsedDate(wsrrutility, bsrURI, url, user, password)).append("@#@");
			
			//30.08.2016 in chiusura del record vengono recuperate altre informazioni indipendentemente se da acronimo o SSA in questo caso sono 
			//le info associate al SSA
			
			if (ssaBV !=null) {
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_ATTIVITA_MATRICOLA")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_ATTIVITA_NOMINATIVO")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_UFFICIO_MATRICOLA")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_UFFICIO_NOMINATIVO")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_SERVIZIO_MATRICOLA")).append("@#@");
			recordSB.append(WSRRUtility.getObjectValueFromJSONArrayData((JSONArray) orgDett.get(0), "ale63_RESP_SERVIZIO_NOMINATIVO"));
			}
			else {
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");
				recordSB.append(" ").append("@#@");
				recordSB.append(" ");
			}
			
			elenco_servizi.println(recordSB.toString());

			recordSB.delete(0, recordSB.length());

			j++;
		}

	  
  }

}
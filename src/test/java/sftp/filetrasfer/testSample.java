package sftp.filetrasfer;

public class testSample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String materialIds =" '000940560133','003620460000','003620460100','003620490517'";
		String selectQueryGeneralData = new String("select gd.* , concat(LEFT(gd.material, 5) ,'-' ,RIGHT(LEFT(gd.material, 10), 5) , '-' , RIGHT(gd.material, 2)) as anart ,\r\n"
				+ "gd.MaterialType as amtyp, \r\n" + "gd.GPCClassification as aextmaterialgrp,\r\n"
				+ "RIGHT(LEFT((gd.material), 10), 5) as asequencenumber,\r\n"
				+ "IIF( gd.InternationalLocalNART ='l','Local',\r\n"
				+ "	IIF(gd.InternationalLocalNART = 'G', 'International',\r\n" + "	   '')\r\n"
				+ "	) as ainternationallocalnart,\r\n" + "\r\n" + "IIF(gd.IndicatorDissolving = 'Y','Yes',\r\n"
				+ "	IIF(gd.IndicatorDissolving = 'N', 'No',\r\n" + "	   '')\r\n" + "	) as aindicatordissolving,\r\n"
				+ "gd.LVVersion  as aproductversionid,\r\n"
				+ "CONCAT(gd.spgr,'-',gd.pv ) as aidentifier,\r\n"
				+ " gd.LVVersion from tb_BS_SAP_PIM_GeneralData  (NOLOCK) as gd where gd.material IN (" + materialIds + ")");
		System.out.println(" Sql query " +  selectQueryGeneralData);
	}

}

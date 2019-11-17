package org.fluentjava.mockodore.model.sid;

import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.AD_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.AD_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.AD_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.CR_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FCHI;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FCLO;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_HI_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_HI_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_HI_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_LO_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_LO_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.FREQ_LO_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.MODE_VOL;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_HI_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_HI_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_HI_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_LO_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_LO_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.PW_LO_3;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.RES_FILT;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.SR_1;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.SR_2;
import static org.fluentjava.mockodore.model.sid.SidRegisterAddress.SR_3;

import java.util.Arrays;
import java.util.List;

public class SidRegisterAddresses {

	/**
	 * TODO analyze other write orders
	 */
	public static List<SidRegisterAddress> allInDefaultWritingOrder() {
		return Arrays.asList(

				AD_1, SR_1, FREQ_HI_1, FREQ_LO_1, PW_HI_1, PW_LO_1, CR_1

				, AD_2, SR_2, FREQ_HI_2, FREQ_LO_2, PW_HI_2, PW_LO_2, CR_2

				, AD_3, SR_3, FREQ_HI_3, FREQ_LO_3, PW_HI_3, PW_LO_3, CR_3

				, FCHI, FCLO, MODE_VOL, RES_FILT

		);
	}

}

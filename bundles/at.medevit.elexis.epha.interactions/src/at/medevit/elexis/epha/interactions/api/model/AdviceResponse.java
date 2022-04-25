package at.medevit.elexis.epha.interactions.api.model;

import java.util.List;
import java.util.Map;

public class AdviceResponse {

	// {
	// "meta": {
	// "code": 201,
	// "status": "Created",
	// "message": "Review created"
	// },
	// "data": {
	// "id": "3350-1318-9dda-242e-e916",
	// "link": "https://epha.health/clinic/advice/de/xid=3350-1318-9dda-242e-e916/",
	// "safety": 81,
	// "risk": {
	// "kinetic": 0,
	// "qtc": 2,
	// "warning": 0,
	// "serotonerg": 0,
	// "anticholinergic": 0,
	// "adverse": 18
	// },
	// "valid": [
	// {
	// "type": "drug",
	// "gtin": "7680543200193"
	// },
	// {
	// "type": "drug",
	// "gtin": "7680558470024"
	// },
	// {
	// "type": "drug",
	// "gtin": "7680480920123"
	// }
	// ],
	// "fails": [],
	// "audit": [
	// {
	// "ip": "::ffff:172.17.0.1",
	// "timestamp": "2021-06-08T12:37:07.122Z"
	// }
	// ]
	// }
	// }

	private Meta meta;
	private Data data;

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public static class Meta {
		private int code;
		private String status;
		private String message;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	public static class Data {
		private String id;
		private String link;
		private int safety;

		private Map<String, String> risk;

		private List<Map<String, String>> valid;

		private List<Map<String, String>> fails;

		private List<Map<String, String>> audit;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public int getSafety() {
			return safety;
		}

		public void setSafety(int safety) {
			this.safety = safety;
		}

		public Map<String, String> getRisk() {
			return risk;
		}

		public void setRisk(Map<String, String> risk) {
			this.risk = risk;
		}

		public List<Map<String, String>> getValid() {
			return valid;
		}

		public void setValid(List<Map<String, String>> valid) {
			this.valid = valid;
		}

		public List<Map<String, String>> getFails() {
			return fails;
		}

		public void setFails(List<Map<String, String>> fails) {
			this.fails = fails;
		}

		public List<Map<String, String>> getAudit() {
			return audit;
		}

		public void setAudit(List<Map<String, String>> audit) {
			this.audit = audit;
		}
	}
}

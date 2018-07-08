package uk.ac.warwick.dcs.sherlock.services.detection;

class Tuple <K, V>{
	private K key;
	private V value;
	public Tuple(K  k, V v){
		this.key = k;
		this.value = v;
	}
	public K getKey(){
		return this.key;
	}
	public V getValue(){
		return this.value;
	}
	public void setKey(K k){
		this.key = k;
	}
	public void setValue(V v){
		this.value = v;
	}
	public boolean same(Tuple<K,V> t2){
		if (this.key.equals(t2.getKey()) && this.value.equals(t2.getValue())){
			return true;
		}return false;
	}
	@Override
	public String toString(){
		return String.valueOf(this.key) + ", " + String.valueOf(this.value);
    }
}
using UnityEngine;
using static UnityEngine.Vector3;

[RequireComponent(typeof(Rigidbody))]
public class Asteroid : MonoBehaviour
{
    private Rigidbody _body;
    public float speed = 6;
    public GameObject explotioneffect;
    private AudioSource _explotion;
    private void Start()
    {
        if (Camera.main != null) _explotion = Camera.main.GetComponent<AudioSource>();
        _body = GetComponent<Rigidbody>();
        var moveZ = speed * back;
        var moveX = right * Random.Range(-1,1);
        
        _body.velocity = moveZ + moveX;
        _body.angularVelocity = new Vector3( Random.Range(-3,3),Random.Range(-3,3),Random.Range(-3,3));
        Destroy(gameObject,5);
    }

    private void Collision()
    {
        Instantiate(explotioneffect, transform.position , explotioneffect.transform.rotation);
       _explotion.Play();
        Destroy(gameObject);
    }
    private void OnTriggerEnter(Collider col)
    {
        if (!col.CompareTag("Player")) return;
        Instantiate(explotioneffect, transform.position , explotioneffect.transform.rotation);
        _explotion.Play();
        Destroy((gameObject));
        col.SendMessage("Damage", SendMessageOptions.DontRequireReceiver);
    }
}

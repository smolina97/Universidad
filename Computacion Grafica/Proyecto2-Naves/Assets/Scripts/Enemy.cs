using System.Collections;
using System.Collections.Generic;
using TMPro;
using static UnityEngine.Vector3;
using UnityEngine;

public class Enemy : MonoBehaviour
{
    public int lives = 3;
    public TextMeshProUGUI livesText;
    private Rigidbody _body;
    public float moveFactor = 0f;
    public float speed = 2f;
    public Transform cannon;
    public GameObject bullet;
    public GameObject explotioneffect;
    public AudioSource shootSound;

    private void Start()
    {
        _body = GetComponent<Rigidbody>();
        StartCoroutine(NumberGen());
       
    }
    
    IEnumerator NumberGen(){
        while(true){
            moveFactor = Random.Range(-1,2);
            shootSound.Play();
            Instantiate(bullet, cannon.position , bullet.transform.rotation );
            yield return new WaitForSeconds(1);
           
           
        }
    }
    
    private void Update()
    {
        var moveX = right * moveFactor;
        _body.velocity =
            speed * moveX;
        
        livesText.text = "X " + lives;
    }
    
    private void Collision()
    {
        lives--;
        if (lives >= 0) return;
        Destroy(gameObject);
        Instantiate(explotioneffect, transform.position , explotioneffect.transform.rotation);
        GameOver.Over.Victory();
    }
}

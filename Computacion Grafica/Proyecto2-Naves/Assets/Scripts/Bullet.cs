using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Bullet : MonoBehaviour
{
    public float speed = 20;

    private void Start()
    {
        Destroy(gameObject, 1);
        
    }

    private void OnTriggerEnter(Collider col)
    {
        if (!col.CompareTag("Enemy")) return;
        Destroy((gameObject));
        col.SendMessage("Collision" , SendMessageOptions.DontRequireReceiver);
        PlayerLivesScore.ActualScore.IncreaseScore();
        
    }

    private void Update()
    {
        var move  = transform;
        move.position += move.up * (speed * Time.deltaTime);

    }
}

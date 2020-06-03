using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Serialization;

public class PlayerShoot : MonoBehaviour
{
    public Transform cannon;
    public GameObject bullet;
    public AudioSource shootSound;
    private void Update()
    {
        if (Input.GetButtonDown("Jump"))
        {
            shootSound.Play();
            Instantiate(bullet, cannon.position , bullet.transform.rotation );
        }
    }
}

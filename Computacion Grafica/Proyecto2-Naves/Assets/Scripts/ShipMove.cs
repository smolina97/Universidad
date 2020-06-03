using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using static UnityEngine.Vector3;

[RequireComponent(typeof(Rigidbody))]
public class ShipMove : MonoBehaviour
{
    private Rigidbody _body;
    public float speed = 8f;

    private void Start()
    {
        _body = GetComponent<Rigidbody>();
    }

    private void Update()
    {
        var moveX = Input.GetAxis("Horizontal") * right;
        _body.velocity =
            speed * moveX;
    }
    
}

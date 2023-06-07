from ansible.module_utils.basic import AnsibleModule

def sayHello(msg):
    return "Hello Custom Module message - " + msg + " !"

def main():
    module = AnsibleModule(
        argument_spec=dict(
            message=dict(type='str'),
        )
    )

    msg = module.params['message']

    result = dict(
        output= sayHello(msg),
    )

    module.exit_json(**result)


if __name__ == '__main__':
    main()


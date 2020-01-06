import { axios } from '@/utils/request'

export function register(parameter) {
  return axios({
    url: '/sys/register',
    method:'post' ,
    data: parameter
  })
}